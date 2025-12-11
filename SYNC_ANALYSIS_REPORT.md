# Rapport d'Analyse : Synchronisation Redis/MySQL - Towns and Nations

**Date**: 11 décembre 2025  
**Projet**: Towns and Nations (Folia)  
**Branche**: feature/sync-fix-redis-mysql

---

## 🎯 Objectifs

1. ✅ Identifier toutes les désynchronisations Redis ⇄ MySQL
2. 🔧 Définir MySQL comme source de vérité primaire
3. 🛡️ Implémenter mécanisme de réconciliation automatique
4. 🐛 Corriger bugs critiques de synchronisation
5. ♻️ Moderniser code obsolète (@Deprecated)
6. ✅ Ajouter tests unitaires/intégration
7. 📊 Monitoring et métriques
8. 📚 Runbook opérationnel

---

## 📊 Architecture Actuelle

### Flux de Données Identifié

```
┌─────────────────────────────────────────────────┐
│          APPLICATION LAYER                       │
│  (Commands, GUIs, Event Handlers)               │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│       DatabaseStorage<T> (Base Class)           │
│  ┌────────────────────────────────────────┐    │
│  │ Local Cache (LinkedHashMap)             │    │
│  │ - LRU eviction                          │    │
│  │ - Configurable size (default: 1000)     │    │
│  │ - Synchronized access                   │    │
│  └────────────────────────────────────────┘    │
└──────────────────┬──────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
┌──────────────┐    ┌──────────────────┐
│   MySQL      │    │ QueryCacheManager│
│  (Primary)   │    │  (Two-Tier)      │
│              │    │                  │
│ - ACID txn   │    │ L1: Guava Cache │
│ - Persistent │    │   (3min TTL)    │
│ - HikariCP   │    │                  │
│              │    │ L2: Redis Hash  │
│              │    │   (5-30min TTL) │
└──────────────┘    └──────────────────┘
        │                     │
        │                     ▼
        │            ┌──────────────────┐
        │            │ RedisSyncManager │
        │            │ (Pub/Sub Events) │
        │            │                  │
        │            │ - Player events  │
        │            │ - Territory sync │
        │            │ - Cache inv.     │
        └────────────┴──────────────────┘
```

### Composants Clés

#### 1. **DatabaseStorage<T>** (Source Primaire)
- **Localisation**: `tan-core/src/main/java/org/leralix/tan/storage/stored/DatabaseStorage.java`
- **Rôle**: Couche d'abstraction pour persistance MySQL + cache L1 local
- **Méthodes**:
  - `get(String id)` → CompletableFuture<T> (async)
  - `putSync(String id, T obj)` → write-through MySQL + cache
  - `putAsync(String id, T obj)` → async write + optimistic cache update
  - `put(String id, T obj)` → **@Deprecated** (utilise putSync)
  - `delete(String id)` → **@Deprecated** (manque invalidation Redis)

#### 2. **QueryCacheManager** (Cache L2)
- **Localisation**: `tan-core/src/main/java/org/leralix/tan/redis/QueryCacheManager.java`
- **Rôle**: Two-tier caching (Guava local + Redis distributed)
- **Stratégie**:
  - **L1 (Guava)**: 3min TTL, 10,000 entries max
  - **L2 (Redis)**: Hash `tan:query_cache` avec TTL variable (5-30min)
- **Problèmes**:
  - Pas de write-through automatique
  - Invalidation manuelle requise
  - Pas de versioning/timestamps pour détecter stale data

#### 3. **RedisSyncManager** (Événements Multi-Serveur)
- **Localisation**: `tan-core/src/main/java/org/leralix/tan/redis/RedisSyncManager.java`
- **Rôle**: Pub/Sub pour synchronisation cross-server
- **Canaux**:
  - `tan:sync:player_data`
  - `tan:sync:territory_data`
  - `tan:sync:cache_invalidation`
- **Types d'événements**: 70+ types (TOWN_LEVEL_UP, PLAYER_BALANCE_UPDATE, etc.)

---

## 🔴 Problèmes Critiques Identifiés

### 1. **Race Condition: Write-After-Write**

**Scénario**:
```java
// Server A
townData.setBalance(1000);
storage.putAsync(townId, townData); // Write 1 (async)

// Server B (simultanément)
townData.setBalance(2000);
storage.putAsync(townId, townData); // Write 2 (async)

// Result: Dernier write gagne, perte de données
```

**Impact**: Perte de modifications concurrentes (balance, membres, upgrades)

**Solution**: 
- Utiliser versioning optimiste (version number/timestamp)
- Transactions ACID pour opérations critiques
- Redis Lua scripts pour atomicité multi-clés

---

### 2. **Missing Cache Invalidation**

**Code Problématique**:
```java
// DatabaseStorage.java:343 - @Deprecated method encore utilisé
@Deprecated
public void put(String id, T obj) {
    putSync(id, obj);
}

// putSync met à jour MySQL + cache local L1
// ❌ MANQUE: Invalidation Redis L2 et broadcast cross-server
```

**Fichiers Affectés**:
- `PlayerDataStorage.put()` - ligne 143
- `TownDataStorage.put()` - ligne 160
- `RegionDataStorage.put()` - ligne 106

**Impact**: 
- Cache stale sur autres serveurs
- Désynchronisation town balance, members, upgrades
- Players voient données obsolètes après join/leave

**Solution**:
```java
public void putSync(String id, T obj) {
    // 1. Write to MySQL (ACID)
    writeToDatabase(id, obj);
    
    // 2. Update local cache
    cache.put(id, obj);
    
    // 3. ✅ AJOUTER: Invalidate Redis L2
    QueryCacheManager.invalidateTerritory(id);
    
    // 4. ✅ AJOUTER: Broadcast to other servers
    if (syncManager != null) {
        syncManager.publishCacheInvalidation("tan:cache:" + id);
    }
}
```

---

### 3. **Non-Atomic Multi-Key Operations**

**Code Problématique**:
```java
// TownSyncService.java - Opération multi-étapes non-atomique
public void publishFullTownDataSync(TownData townData) {
    JsonObject payload = new JsonObject();
    payload.addProperty("townId", townData.getID());
    payload.addProperty("townLevel", townData.getNewLevel().getMainLevel());
    
    // Step 1: Publish event
    syncManager.publishTerritoryDataChange(...);
    
    // ❌ Si crash ici, inconsistency entre event et cache
    
    // Step 2: Update cache (ailleurs dans le code)
    storage.putAsync(townId, townData);
}
```

**Solution**: Redis Lua script pour garantir atomicité

```lua
-- atomic_cache_update.lua
local key = KEYS[1]
local data = ARGV[1]
local ttl = ARGV[2]

redis.call('HSET', 'tan:query_cache', key, data)
redis.call('EXPIRE', 'tan:query_cache', ttl)
redis.call('PUBLISH', 'tan:sync:cache_invalidation', key)

return 1
```

---

### 4. **Stale Data Detection: Aucun Mécanisme**

**Problème**: Aucun timestamp/version pour détecter données obsolètes

**Exemple**:
```java
// Pas de vérification si données Redis sont plus récentes que MySQL
TerritoryData cached = queryCache.getTerritoryData(id, ...);
TerritoryData fromDB = storage.get(id).join();

// ❌ Aucune comparaison, aucune réconciliation automatique
```

**Solution**: Ajouter `lastModified` timestamp partout

```java
public abstract class SyncedEntity {
    private long lastModified;
    private int version;
    
    public void touch() {
        this.lastModified = System.currentTimeMillis();
        this.version++;
    }
}

// Dans storage
public T get(String id) {
    T cached = getCached(id);
    T fromDB = loadFromDatabase(id);
    
    if (cached != null && fromDB != null) {
        // Réconciliation: prendre le plus récent
        return cached.getLastModified() > fromDB.getLastModified() 
            ? cached : fromDB;
    }
    return fromDB != null ? fromDB : cached;
}
```

---

### 5. **Transaction Boundaries: Manquantes**

**Code Problématique**:
```java
// TerritoryData.java - Upgrade town level
public void upgradeTownLevel() {
    int oldLevel = getNewLevel().getMainLevel();
    getNewLevel().levelUpMain();
    int newLevel = getNewLevel().getMainLevel();
    
    // ❌ Pas de transaction wrappant:
    // - Deduct player balance
    // - Update town level
    // - Add upgrade
    // - Update storage
    
    TownDataStorage.getInstance().put(this.id, townData);
    syncService.publishTownLevelUp(...);
}
```

**Impact**: Crash entre deduction + upgrade = argent perdu, pas d'upgrade

**Solution**:
```java
@Transactional
public void upgradeTownLevel() throws InsufficientFundsException {
    Connection conn = getConnection();
    try {
        conn.setAutoCommit(false);
        
        // 1. Deduct balance
        playerBalance -= cost;
        playerStorage.putSync(playerId, player);
        
        // 2. Apply upgrade
        getNewLevel().levelUpMain();
        
        // 3. Save town
        townStorage.putSync(townId, town);
        
        conn.commit();
        
        // 4. Post-commit: Sync to Redis
        syncManager.publishFullTownDataSync(town);
        
    } catch (Exception e) {
        conn.rollback();
        throw e;
    }
}
```

---

## 🔧 Méthodes @Deprecated à Retirer

### Liste Complète

| Fichier | Méthode | Utilisations | Remplacement |
|---------|---------|--------------|--------------|
| `DatabaseStorage.java:228` | `getAll()` | 5x | `getAllAsync()` |
| `DatabaseStorage.java:342` | `put(String, T)` | 38x | `putAsync()` + cache inv. |
| `DatabaseStorage.java:511` | `delete(String)` | 12x | `deleteAsync()` + cache inv. |
| `NewClaimedChunkStorage.java:54` | `loadChunkData()` | 3x | `loadChunkDataAsync()` |
| `TerritoryUtil.java:14` | `getTerritory(String)` | 100+ | `TerritoryStorage.get()` |
| `Lang.java:1188` | `get(Player)` | 200+ | `get(ITanPlayer)` |
| `LangType.java:122` | `of(Player)` | 15x | `of(ITanPlayer)` |

### Plan de Migration

#### Étape 1: Ajouter méthodes de remplacement sécurisées
```java
// DatabaseStorage.java
public CompletableFuture<Void> putWithInvalidation(String id, T obj) {
    return putAsync(id, obj)
        .thenRun(() -> QueryCacheManager.invalidateTerritory(id))
        .thenRun(() -> syncManager.publishCacheInvalidation("tan:cache:" + id));
}
```

#### Étape 2: Remplacer toutes les utilisations
```bash
# Script de migration automatique
find tan-core/src -name "*.java" -exec sed -i 's/storage\.put(/storage.putWithInvalidation(/g' {} +
```

#### Étape 3: Marquer forRemoval + compiler
```java
@Deprecated(since = "0.17.0", forRemoval = true)
public void put(String id, T obj) {
    throw new UnsupportedOperationException("Use putWithInvalidation()");
}
```

---

## 🛡️ Source de Vérité: Stratégie Définie

### Règles

| Type de Donnée | Source Primaire | Cache | Raison |
|----------------|-----------------|-------|--------|
| **Player Balance** | MySQL | Redis 1min TTL | Transactions fréquentes, ACID requis |
| **Town Data** | MySQL | Redis 5min TTL | Modifications rares, lecture fréquente |
| **Chunk Ownership** | MySQL | Redis 10min TTL | Très stable, lecture intensive |
| **Transaction History** | MySQL | Redis 30min TTL | Immuable après création |
| **Online Players** | Redis ONLY | N/A | Éphémère, pas de persistance |
| **Active Wars** | MySQL + Redis | Redis 30sec TTL | Coordination temps réel |

### Mécanisme de Réconciliation

**Cron Job: DataConsistencyChecker**

```java
@Scheduled(fixedRate = 300000) // Every 5 minutes
public void checkConsistency() {
    List<String> townIds = getAllTownIds();
    
    for (String townId : townIds) {
        // 1. Load from MySQL
        TownData fromDB = townStorage.getSync(townId);
        
        // 2. Load from Redis
        TownData fromCache = queryCache.getTerritoryData(townId, id -> null);
        
        // 3. Compare
        if (fromDB != null && fromCache != null) {
            if (fromDB.getLastModified() > fromCache.getLastModified()) {
                // MySQL is newer → invalidate cache
                logger.warning("DIVERGENCE: Town " + townId + " - MySQL newer");
                queryCache.invalidateTerritory(townId);
                metricsCollector.incrementDivergence("town_data");
            } else if (fromCache.getLastModified() > fromDB.getLastModified()) {
                // Cache is newer → write back to MySQL (write-behind)
                logger.warning("DIVERGENCE: Town " + townId + " - Cache newer");
                townStorage.putSync(townId, fromCache);
                metricsCollector.incrementDivergence("town_data");
            }
        }
    }
}
```

---

## ✅ Plan d'Action Priorisé

### Phase 1: Corrections Critiques (Jours 1-3)

#### 1.1 Ajouter Timestamps/Versioning
- [ ] Créer classe `SyncedEntity` avec `lastModified` et `version`
- [ ] Étendre `TownData`, `ITanPlayer`, `RegionData`
- [ ] Migrer données existantes (SQL update script)

#### 1.2 Fixer Missing Cache Invalidation
- [ ] Wrapper `putWithInvalidation()` dans `DatabaseStorage`
- [ ] Remplacer 38 usages de `put()` deprecated
- [ ] Ajouter tests de non-régression

#### 1.3 Implémenter Transactions ACID
- [ ] Wrapper `@Transactional` pour upgrades
- [ ] Wrapper pour balance transfers
- [ ] Tests rollback/retry

### Phase 2: Réconciliation Automatique (Jours 4-5)

#### 2.1 Consistency Checker
- [ ] Créer `DataConsistencyService`
- [ ] Impl background job (5min interval)
- [ ] Auto-repair avec logs détaillés

#### 2.2 Circuit Breaker Pattern
- [ ] Wrapper Resilience4j pour Redis ops
- [ ] Fallback sur MySQL si Redis down
- [ ] Métriques failures

### Phase 3: Modernisation Code (Jours 6-7)

#### 3.1 Retirer @Deprecated
- [ ] Script migration automatique
- [ ] Compiler avec `-Werror` deprecation
- [ ] Update documentation

#### 3.2 Async Patterns Cohérents
- [ ] Convertir toutes operations sync → async
- [ ] CompletableFuture chains
- [ ] Error handling unifié

### Phase 4: Tests & Monitoring (Jours 8-10)

#### 4.1 Tests Unitaires
- [ ] `DatabaseStorageTest` - race conditions
- [ ] `QueryCacheManagerTest` - L1/L2 invalidation
- [ ] `RedisSyncManagerTest` - pub/sub delivery

#### 4.2 Tests d'Intégration
- [ ] Multi-server sync scenarios
- [ ] Chaos engineering (kill Redis mid-write)
- [ ] Load testing (1000 concurrent writes)

#### 4.3 Monitoring & Metrics
- [ ] Micrometer metrics: cache hit rate, divergences
- [ ] Grafana dashboards
- [ ] Alerting: divergence count > 10

### Phase 5: CI/CD & Runbook (Jours 11-12)

#### 5.1 GitHub Actions
- [ ] Tests automatiques sur PR
- [ ] Spotless format check
- [ ] JaCoCo coverage ≥ 70%
- [ ] Static analysis (SpotBugs, Checkstyle)

#### 5.2 Runbook Opérationnel
- [ ] Guide diagnostic divergences
- [ ] Commandes admin (`/tan cache status`)
- [ ] Procédure recovery après crash
- [ ] Playbooks incidents

---

## 📊 Métriques à Surveiller

```java
public class SyncMetrics {
    // Cache Performance
    Counter cacheHitsL1;
    Counter cacheMissesL1;
    Counter cacheHitsL2;
    Counter cacheMissesL2;
    
    // Divergence Detection
    Counter divergencesDetected;
    Counter divergencesRepaired;
    Timer reconciliationDuration;
    
    // Write Operations
    Counter writesMySQL;
    Counter writesRedis;
    Counter writeFail
ures;
    Timer writeLatencyMySQL;
    Timer writeLatencyRedis;
    
    // Sync Events
    Counter syncEventsPublished;
    Counter syncEventsReceived;
    Counter syncEventFailures;
}
```

---

## 🔍 Commandes Diagnostic

```bash
# Status général
/tan cache status
> L1 Cache - Hits: 45231 | Misses: 1230 | Hit Rate: 97.3% | Size: 8432
> L2 Cache - Entries: 3421 | Divergences: 2 | Last Check: 2min ago

# Vérifier town spécifique
/tan sync check <townId>
> MySQL: level=5, balance=10000, lastModified=1702300000
> Redis: level=4, balance=9500, lastModified=1702299500
> ⚠ DIVERGENCE DETECTED: MySQL is 500ms newer
> Action: Invalidating Redis cache...

# Forcer réconciliation
/tan sync reconcile --all
> Checking 234 towns...
> ✓ 232 consistent
> ⚠ 2 divergences fixed

# Clear cache (emergency)
/tan cache clear --confirm
> Clearing L1 (Guava)... Done
> Clearing L2 (Redis)... Done
> ⚠ Next reads will hit MySQL
```

---

## 📝 Changelog (à créer)

```markdown
# CHANGELOG - Synchronisation Redis/MySQL Fix

## [0.18.0] - 2025-12-15

### 🔴 BREAKING CHANGES
- Méthodes deprecated retirées: `put()`, `delete()`, `getAll()`
- Migration requise: utiliser `putWithInvalidation()`, `deleteAsync()`

### ✨ Added
- Versioning optimiste (lastModified + version)
- Réconciliation automatique (background job 5min)
- Circuit breaker pour Redis ops
- Metrics Micrometer (cache, divergences, writes)
- Admin commands: `/tan cache`, `/tan sync`

### 🐛 Fixed
- Race condition write-after-write (versioning)
- Missing cache invalidation sur 38 put() calls
- Non-atomic multi-key operations (Lua scripts)
- Transaction boundaries pour upgrades/transfers
- Stale data detection

### ♻️ Refactored
- Async patterns cohérents (CompletableFuture)
- Error handling unifié
- Code mort supprimé (1200+ lignes)

### ✅ Tests
- 45 nouveaux tests unitaires
- 12 tests d'intégration multi-serveur
- Coverage: 68% → 82%

### 📚 Documentation
- Runbook opérationnel (30 pages)
- Architecture diagrams (Mermaid)
- Grafana dashboards export
```

---

## 🚀 Prochaines Étapes

**Maintenant**: Review de ce rapport par l'équipe

**Phase 1 Start**: Implémenter timestamps + cache invalidation fixes

**ETA Prod**: 12 jours (si pas de blockers)

---

**Status**: ✅ Analyse complète  
**Reviewers**: @TsumunDev, @team  
**Priority**: 🔴 CRITICAL
