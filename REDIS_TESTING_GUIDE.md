# Guide de Test - Configuration Redis

## 🧪 Scénarios de Test

### Test 1: Redis sans Authentification (Configuration par défaut)

**Configuration `config.yml`:**
```yaml
redis:
  enabled: true
  mode: "single"
  server-id: "survival-1"
  single:
    host: "localhost"
    port: 6379
  # password: ""  # ← COMMENTEZ cette ligne pour Redis sans mot de passe
  database: 0
```

**Démarrage de Redis (sans mot de passe):**
```bash
# Windows (avec Redis installé via WSL ou chocolatey)
redis-server

# Linux/Mac
redis-server
```

**Logs Attendus:**
```
[INFO] 🆔 Server ID: survival-1
[INFO] 📡 Connexion Redis: localhost:6379 (DB: 0)
[INFO] Redis: Pas d'authentification (mot de passe non configuré)
[INFO] ✓ Client Redis initialisé
[INFO] ✓ Test de connexion Redis réussi
[INFO] ⇄ Sync multi-serveur activé: survival-1
[INFO] 🌐 Serveurs actifs: survival-1
```

**Vérification:**
```bash
# Depuis redis-cli, vérifiez les clés créées
redis-cli
> KEYS tan:*
1) "tan:heartbeat:survival-1"
2) "tan:active-servers"
> GET tan:heartbeat:survival-1
"1702159825000"
> SMEMBERS tan:active-servers
1) "survival-1"
```

---

### Test 2: Redis avec Mot de Passe

**Démarrage de Redis avec mot de passe:**
```bash
# Linux/Mac - Éditer redis.conf
sudo nano /etc/redis/redis.conf
# Ajouter: requirepass votre_mot_de_passe

# Redémarrer
sudo systemctl restart redis

# Ou démarrer avec config
redis-server --requirepass testpass123
```

**Configuration `config.yml`:**
```yaml
redis:
  enabled: true
  mode: "single"
  server-id: "survival-1"
  single:
    host: "localhost"
    port: 6379
  password: "testpass123"  # ← Votre mot de passe
  database: 0
```

**Logs Attendus:**
```
[INFO] 🆔 Server ID: survival-1
[INFO] 📡 Connexion Redis: localhost:6379 (DB: 0)
[INFO] Redis: Authentification activée (mot de passe fourni)
[INFO] ✓ Client Redis initialisé
[INFO] ✓ Test de connexion Redis réussi
[INFO] ⇄ Sync multi-serveur activé: survival-1
```

**Vérification:**
```bash
redis-cli
> AUTH testpass123
OK
> KEYS tan:*
1) "tan:heartbeat:survival-1"
```

---

### Test 3: Mauvais Mot de Passe (Test d'Erreur)

**Configuration `config.yml`:**
```yaml
redis:
  password: "mauvais_mot_de_passe"
```

**Logs Attendus (ERREUR):**
```
[INFO] 🆔 Server ID: survival-1
[INFO] 📡 Connexion Redis: localhost:6379 (DB: 0)
[INFO] Redis: Authentification activée (mot de passe fourni)
[INFO] ✓ Client Redis initialisé
[ERROR] ✖ Échec test connexion Redis: WRONGPASS invalid username-password pair
[ERROR] Vérifiez: 1) Redis est démarré, 2) host/port corrects, 3) mot de passe valide
[ERROR] ✖ ERREUR Redis: WRONGPASS invalid username-password pair or user is disabled.
[ERROR] Erreur d'authentification - Vérifiez le mot de passe dans config.yml
[ERROR] Utilisez password: null (ou commentez) si Redis n'a pas de mot de passe
```

**Action:** Corriger le mot de passe dans `config.yml` et relancer le serveur.

---

### Test 4: Redis non Démarré (Test d'Erreur)

**Configuration:** Redis correct mais serveur Redis éteint

**Logs Attendus (ERREUR):**
```
[INFO] 🆔 Server ID: survival-1
[INFO] 📡 Connexion Redis: localhost:6379 (DB: 0)
[INFO] Redis: Pas d'authentification (mot de passe non configuré)
[INFO] ✓ Client Redis initialisé
[ERROR] ✖ Échec test connexion Redis: Unable to connect to Redis server: localhost/127.0.0.1:6379
[ERROR] Vérifiez: 1) Redis est démarré, 2) host/port corrects, 3) mot de passe valide
[ERROR] ✖ ERREUR: Impossible de se connecter à Redis
[ERROR] Cause: Unable to connect to Redis server: localhost/127.0.0.1:6379
[ERROR] Solutions: Vérifiez que Redis est démarré et accessible sur localhost:6379
```

**Action:** Démarrer Redis avec `redis-server`

---

### Test 5: Multi-Serveur (2 serveurs en même temps)

**Serveur 1 - config.yml:**
```yaml
redis:
  enabled: true
  server-id: "survival-1"  # ← Unique par serveur
  single:
    host: "localhost"
    port: 6379
```

**Serveur 2 - config.yml:**
```yaml
redis:
  enabled: true
  server-id: "creative-1"  # ← DIFFÉRENT du serveur 1
  single:
    host: "localhost"
    port: 6379
```

**Logs Attendus (Serveur 1):**
```
[INFO] 🆔 Server ID: survival-1
[INFO] 🌐 Serveurs actifs: survival-1
```

**Logs Attendus (Serveur 2 après démarrage):**
```
[INFO] 🆔 Server ID: creative-1
[INFO] 🌐 Serveur creative-1: CONNECTED
[INFO] 🌐 Serveurs actifs: survival-1, creative-1
```

**Logs sur Serveur 1 (détection du nouveau serveur):**
```
[INFO] 🌐 Serveur creative-1: CONNECTED
```

**Vérification:**
```bash
redis-cli
> SMEMBERS tan:active-servers
1) "survival-1"
2) "creative-1"
```

---

## 🐛 Diagnostic des Problèmes

### Le plugin ne démarre pas

**Symptômes:** Plugin désactivé au démarrage

**Checklist:**
1. Vérifier les logs pour les erreurs JDBC/Database
2. Vérifier que la base de données est accessible
3. Si Redis est activé, vérifier qu'il est démarré
4. Vérifier les permissions du fichier de configuration

### Redis se connecte mais pas de synchronisation

**Symptômes:** `✓ Client Redis initialisé` mais pas de synchronisation visible

**Checklist:**
1. Vérifier que `server-id` est unique pour chaque serveur
2. Vérifier que les deux serveurs utilisent le même Redis
3. Vérifier les clés Redis: `redis-cli KEYS tan:*`
4. Vérifier les logs pour les erreurs de pub/sub

### Problèmes de performance

**Symptômes:** Lag, timeout Redis

**Solutions:**
1. Augmenter `connection.timeout` dans config.yml (default: 3000ms)
2. Augmenter `connection.retry-attempts` (default: 3)
3. Vérifier la latence réseau vers Redis: `redis-cli --latency`
4. Considérer un Redis local pour chaque serveur

---

## 📊 Commandes de Vérification

### Vérifier la Connexion Redis
```bash
# Test de ping
redis-cli ping
# Réponse attendue: PONG

# Vérifier l'authentification
redis-cli -a votre_mot_de_passe ping
# Réponse attendue: PONG
```

### Vérifier les Clés TaN
```bash
redis-cli
> KEYS tan:*
> SMEMBERS tan:active-servers
> TTL tan:heartbeat:survival-1
> GET tan:heartbeat:survival-1
```

### Surveiller les Événements en Temps Réel
```bash
redis-cli
> SUBSCRIBE tan:server-events
# Vous verrez les messages quand des serveurs se connectent/déconnectent
```

### Nettoyer les Données de Test
```bash
redis-cli
> KEYS tan:*
> DEL tan:heartbeat:survival-1
> DEL tan:active-servers
> FLUSHDB  # ⚠️ ATTENTION: Efface TOUTE la base de données
```

---

## 🎯 Résumé des Indicateurs de Succès

### ✅ Configuration Correcte
- [x] Plugin démarre sans erreur
- [x] Logs montrent "✓ Test de connexion Redis réussi"
- [x] `redis-cli KEYS tan:*` montre des clés actives
- [x] Heartbeat visible: `GET tan:heartbeat:server-id`
- [x] Serveur enregistré: `SMEMBERS tan:active-servers`

### ❌ Configuration Incorrecte
- [ ] Erreurs "WRONGPASS" dans les logs
- [ ] Erreurs "Unable to connect" dans les logs
- [ ] Pas de clés `tan:*` dans Redis
- [ ] Plugin désactivé au démarrage
- [ ] Timeouts fréquents

---

## 📝 Notes Importantes

1. **Server ID Unique**: Chaque serveur DOIT avoir un `server-id` différent
2. **Redis Persistance**: Par défaut, Redis peut perdre des données au redémarrage. Configurez `save` dans redis.conf pour la persistance
3. **Firewall**: Si Redis est sur un serveur distant, ouvrez le port 6379
4. **Sécurité**: En production, TOUJOURS utiliser un mot de passe fort pour Redis
5. **Performance**: Redis doit être sur le même réseau local que les serveurs Minecraft pour de bonnes performances

---

## 🔧 Dépannage Avancé

### Logs Détaillés
Pour activer les logs Redisson détaillés, ajoutez dans `logback.xml`:
```xml
<logger name="org.redisson" level="DEBUG"/>
```

### Test de Latence Redis
```bash
redis-cli --latency
# Objectif: < 10ms pour réseau local
```

### Surveiller les Connexions
```bash
redis-cli
> CLIENT LIST
# Montre toutes les connexions actives
```

### Vérifier la Mémoire Redis
```bash
redis-cli
> INFO memory
# Surveiller used_memory_human
```
