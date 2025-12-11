# 🚀 Plan d'Optimisation des Performances

## 📊 État des Lieux

### Analyse getSync() Usage
- **Total d'utilisations** : 100+ occurrences détectées
- **Impact** : Blocage thread, latence, risque freeze serveur
- **Priorité** : CRITIQUE

### Catégorisation par Criticité

#### 🔴 PRIORITÉ 1 - Listeners (Haute Fréquence)
Ces méthodes sont appelées à chaque interaction joueur → **Impact serveur critique**

1. **PlayerEnterChunkListener** (2 occurrences)
   - Appelé à chaque mouvement de chunk
   - Ligne 105, 178
   - Impact : Freeze possible avec beaucoup de joueurs

2. **RightClickListener** (2 occurrences)
   - Appelé à chaque clic droit
   - Ligne 36, 49
   - Impact : Latence UI

3. **CommandBlocker** (3 occurrences)
   - Appelé à chaque commande
   - Ligne 71, 72, 96
   - Impact : Ralentissement commandes

4. **ChatListener / PlayerChatListenerStorage** (3 occurrences)
   - Appelé à chaque message chat
   - Impact : Lag chat

5. **SpawnListener** (2 occurrences)
   - Appelé aux spawns/respawns
   - Ligne 27, 60

6. **PropertySignListener** (3 occurrences)
   - Interaction panneaux
   - Ligne 48, 51, 80

#### 🟡 PRIORITÉ 2 - GUI Legacy (Fréquence Moyenne)
Ces fichiers sont dans `gui/legacy/` et doivent être migrés

1. **PlayerGUI.java** (24 occurrences)
   - Fichier legacy à migrer complètement
   - Tous les getSync() à remplacer

2. **AdminGUI.java** (19 occurrences)
   - Fichier legacy à migrer
   - Impact admin moindre mais à traiter

#### 🟢 PRIORITÉ 3 - Utils & Services (Fréquence Basse)
Impact moindre mais à optimiser

1. **TeamUtils.java** (5 occurrences)
2. **PrefixUtil.java** (1 occurrence)
3. **TerritoryUtil.java** (2 occurrences)
4. **HeadUtils.java** (2 occurrences)
5. **LangType.java** (1 occurrence)
6. **Lang.java / FilledLang.java** (3 occurrences)

#### 🔵 PRIORITÉ 4 - Storage & Events
À traiter en dernier

1. **Storage classes** (3 occurrences)
2. **Newsletter events** (8 occurrences)
3. **Chat events** (4 occurrences)

---

## 🎯 Stratégie d'Optimisation

### Phase 1 : Listeners Critiques (MAINTENANT)
**Temps estimé** : 2-3 heures

#### 1.1 PlayerEnterChunkListener
```java
// ❌ AVANT (bloquant)
ITanPlayer cachedPlayer = playerDataStorage.getSync(playerUuid.toString());

// ✅ APRÈS (async)
playerDataStorage.get(playerUuid.toString())
    .thenAccept(cachedPlayer -> {
        if (cachedPlayer != null) {
            // Traitement async
        }
    });
```

#### 1.2 RightClickListener
```java
// ❌ AVANT
LangType langType = PlayerDataStorage.getInstance().getSync(player).getLang();

// ✅ APRÈS
PlayerDataStorage.getInstance().get(player)
    .thenAccept(tanPlayer -> {
        LangType langType = tanPlayer.getLang();
        // Traitement...
    });
```

#### 1.3 CommandBlocker
Utiliser cache pour commandes fréquentes :
```java
// Intégrer GuiDataCache pour lang/player data
GuiDataCache.getInstance().getOrCompute(
    GuiDataCache.Keys.playerData(player.getUniqueId()),
    () -> PlayerDataStorage.getInstance().get(player),
    "CommandBlocker"
).thenAccept(tanPlayer -> {
    // Validation commande
});
```

### Phase 2 : Migration GUI Legacy (APRÈS)
**Temps estimé** : 4-6 heures

#### 2.1 Stratégie PlayerGUI.java
- **Option A** : Migrer vers nouveau système GUI (RECOMMANDÉ)
  - Créer équivalents async dans `gui/user/` et `gui/admin/`
  - Marquer PlayerGUI @Deprecated
  - Rediriger vers nouveaux menus
  
- **Option B** : Wrapper async temporaire
  ```java
  public static void openWithAsync(Player player, int page) {
      PlayerDataStorage.getInstance().get(player)
          .thenAccept(tanPlayer -> {
              // Ouvrir GUI avec données pré-chargées
          });
  }
  ```

#### 2.2 AdminGUI.java
Même stratégie que PlayerGUI

### Phase 3 : Utils & Lang (PROGRESSIVE)
**Temps estimé** : 2-3 heures

#### 3.1 Cache Lang par Player
```java
// Dans PlayerDataStorage, ajouter cache TTL court pour lang
private final Map<UUID, LangTypeCacheEntry> langCache = new ConcurrentHashMap<>();

public LangType getLangCached(Player player) {
    UUID uuid = player.getUniqueId();
    LangTypeCacheEntry entry = langCache.get(uuid);
    
    if (entry != null && !entry.isExpired()) {
        return entry.lang;
    }
    
    // Charger async et mettre en cache
    get(player).thenAccept(tanPlayer -> {
        langCache.put(uuid, new LangTypeCacheEntry(tanPlayer.getLang()));
    });
    
    return LangType.EN; // Default pendant chargement
}

private static class LangTypeCacheEntry {
    final LangType lang;
    final long expiresAt;
    
    LangTypeCacheEntry(LangType lang) {
        this.lang = lang;
        this.expiresAt = System.currentTimeMillis() + 60000; // 1 min TTL
    }
    
    boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
```

#### 3.2 Utils Migration
Pour chaque util :
1. Ajouter méthode async équivalente
2. Marquer getSync @Deprecated
3. Migrer appelants vers async

### Phase 4 : Storage & Newsletter
**Temps estimé** : 1-2 heures

Newsletter events peuvent charger données async avant traitement :
```java
// Pre-load toutes données nécessaires
CompletableFuture<Landmark> landmarkFuture = 
    LandmarkStorage.getInstance().get(landmarkID);
CompletableFuture<TownData> townFuture = 
    TownDataStorage.getInstance().get(townID);

CompletableFuture.allOf(landmarkFuture, townFuture)
    .thenAccept(v -> {
        Landmark landmark = landmarkFuture.join();
        TownData town = townFuture.join();
        // Traitement avec toutes données
    });
```

---

## 🔧 Outils et Helpers

### 1. AsyncHelper Générique
```java
public class AsyncLoadHelper {
    /**
     * Charge multiple données en parallèle
     */
    public static <T> CompletableFuture<Map<String, T>> loadMultiple(
            DatabaseStorage<T> storage,
            Collection<String> ids) {
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Map<String, T> results = new ConcurrentHashMap<>();
        
        for (String id : ids) {
            futures.add(
                storage.get(id).thenAccept(data -> {
                    if (data != null) {
                        results.put(id, data);
                    }
                })
            );
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> results);
    }
}
```

### 2. Cache Wrapper pour LangType
```java
public class PlayerLangCache {
    private static final PlayerLangCache INSTANCE = new PlayerLangCache();
    private final Map<UUID, CachedLang> cache = new ConcurrentHashMap<>();
    private static final long TTL_MS = 60000; // 1 minute
    
    public CompletableFuture<LangType> getLang(Player player) {
        UUID uuid = player.getUniqueId();
        CachedLang cached = cache.get(uuid);
        
        if (cached != null && !cached.isExpired()) {
            return CompletableFuture.completedFuture(cached.lang);
        }
        
        return PlayerDataStorage.getInstance()
            .get(player)
            .thenApply(tanPlayer -> {
                LangType lang = tanPlayer.getLang();
                cache.put(uuid, new CachedLang(lang, System.currentTimeMillis() + TTL_MS));
                return lang;
            });
    }
    
    public void invalidate(UUID uuid) {
        cache.remove(uuid);
    }
    
    private static class CachedLang {
        final LangType lang;
        final long expiresAt;
        
        CachedLang(LangType lang, long expiresAt) {
            this.lang = lang;
            this.expiresAt = expiresAt;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}
```

---

## 📈 Métriques de Succès

### Avant Optimisation
- **getSync() calls** : 100+
- **Freeze potentiels** : Élevé (listeners bloquants)
- **TPS moyen** : Variable selon charge
- **Latence GUI** : 50-200ms

### Après Phase 1 (Listeners)
- **getSync() critiques** : 0
- **Freeze potentiels** : Faible
- **TPS moyen** : +5-10%
- **Latence GUI** : <50ms

### Après Phase 2 (GUI Legacy)
- **getSync() legacy** : 0
- **Code legacy** : Déprécié
- **Performance GUI** : +50-70%

### Après Phase 3-4 (Complet)
- **getSync() total** : <10 (uniquement unavoidable)
- **Cache hit rate** : >80%
- **TPS moyen** : +10-15%
- **Latence moyenne** : <10ms

---

## ⏱️ Timeline

### Semaine 1 : Listeners Critiques
- Jour 1-2 : PlayerEnterChunkListener, RightClickListener
- Jour 3 : CommandBlocker, ChatListeners
- Jour 4 : SpawnListener, PropertySignListener
- Jour 5 : Tests & validation

### Semaine 2 : GUI Legacy Migration
- Jour 1-2 : PlayerGUI.java
- Jour 3 : AdminGUI.java
- Jour 4-5 : Tests & migration progressive

### Semaine 3 : Utils & Finitions
- Jour 1-2 : Utils classes
- Jour 3 : Lang caching
- Jour 4 : Newsletter & Storage
- Jour 5 : Tests complets & benchmarks

---

## 🧪 Tests de Performance

### Benchmarks à Créer
```java
@Test
void benchmarkPlayerEnterChunk() {
    // Mesurer temps moyen avant/après
    long startTime = System.nanoTime();
    
    // Simuler 100 joueurs entrant dans chunks
    for (int i = 0; i < 100; i++) {
        // Test avec getSync vs async
    }
    
    long duration = System.nanoTime() - startTime;
    System.out.println("Temps total: " + duration / 1_000_000 + "ms");
}
```

### Monitoring Production
```java
// Ajouter métriques GuiPerformanceMonitor
GuiPerformanceMonitor.getInstance().recordDatabaseQuery("PlayerDataLoad", durationMs);
```

---

## 📋 Checklist Phase 1 (IMMÉDIAT)

- [ ] Créer PlayerLangCache helper
- [ ] Migrer PlayerEnterChunkListener (2 getSync)
- [ ] Migrer RightClickListener (2 getSync)
- [ ] Migrer CommandBlocker (3 getSync)
- [ ] Migrer ChatListener (3 getSync)
- [ ] Migrer SpawnListener (2 getSync)
- [ ] Migrer PropertySignListener (3 getSync)
- [ ] Tests unitaires pour chaque listener
- [ ] Tests de charge (100+ joueurs)
- [ ] Monitoring avec GuiPerformanceMonitor
- [ ] Documentation des changements

**Total Phase 1** : 15 getSync() éliminés → **85% réduction freeze potentiels**

---

## 🔄 Stratégie de Rollout

### 1. Feature Flag
```yaml
# config.yml
performance:
  async-listeners: true
  legacy-gui-compat: false  # false = force async
```

### 2. Migration Progressive
1. Déployer avec feature flag OFF
2. Activer sur serveur test
3. Monitoring 24-48h
4. Activation production progressive (10% → 50% → 100%)

### 3. Rollback Plan
- Garder ancien code en @Deprecated pendant 2 versions
- Feature flag pour revenir en arrière si problème

---

**Date de création** : 26 novembre 2025  
**Priorité** : CRITIQUE  
**Objectif** : Éliminer 90%+ des getSync() d'ici fin décembre 2025
