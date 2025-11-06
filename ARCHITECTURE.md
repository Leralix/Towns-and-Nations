# Towns and Nations - Architecture Documentation

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Structure modulaire](#structure-modulaire)
3. [Architecture des packages](#architecture-des-packages)
4. [API publique](#api-publique)
5. [Système économique](#système-économique)
6. [Gestion des territoires](#gestion-des-territoires)
7. [Système de GUI](#système-de-gui)
8. [Base de données et stockage](#base-de-données-et-stockage)
9. [Tests et qualité du code](#tests-et-qualité-du-code)
10. [Migration Folia](#migration-folia)

---

## Vue d'ensemble

**Towns and Nations** est un plugin Minecraft pour serveurs Folia/Paper qui permet aux joueurs de créer et gérer des villes (towns) et régions (nations). Le plugin a été migré depuis Bukkit/Spigot vers Folia pour supporter le multi-threading.

### Caractéristiques principales

- **Système de villes et régions** : Création, gestion et diplomatie entre territoires
- **Économie intégrée** : Système économique avec support Vault
- **Système de chunks** : Claim de chunks avec permissions granulaires
- **Système de guerres** : Attaques planifiées et système de combat territorial
- **Système de rangs** : Hiérarchie personnalisable dans les villes
- **API publique** : API complète pour les développeurs tiers

### Technologies utilisées

- **Java 21** : Version minimale requise
- **Folia API 1.20.1** : Support multi-threading
- **Gradle 8.14** : Build system
- **JUnit 5** : Framework de tests
- **Mockito** : Mocking pour les tests
- **JaCoCo** : Couverture de code
- **Spotless** : Formatage automatique du code

---

## Structure modulaire

Le projet est organisé en deux modules Gradle principaux :

```
Towns-and-Nations/
├── tan-api/           # API publique pour développeurs tiers
│   └── src/main/java/org/tan/api/
└── tan-core/          # Implémentation du plugin
    ├── src/main/java/org/leralix/tan/
    └── src/test/java/org/leralix/tan/
```

### Module `tan-api`

**Responsabilité** : Fournir une API stable et documentée pour les développeurs externes.

**Contenu** :
- Interfaces publiques (EconomyAPI, TownAPI, NationAPI, ClaimAPI)
- Events personnalisés (TownCreateEvent, TownJoinEvent, WarDeclareEvent)
- Interfaces de données (TanPlayer, TanTown, TanNation, TanClaimedChunk)

**Dépendances** : Aucune dépendance sur tan-core (API indépendante)

### Module `tan-core`

**Responsabilité** : Implémentation complète du plugin.

**Contenu** :
- Implémentations des APIs
- Logique métier
- Gestion de la base de données
- Commandes et listeners
- GUIs et cosmétiques

**Dépendances** :
- tan-api
- Folia API
- Bibliothèques tierces (Vault, PlaceholderAPI, WorldGuard, etc.)

---

## Architecture des packages

### Structure principale (`tan-core`)

```
org.leralix.tan/
├── commands/               # Commandes du plugin
│   ├── admin/             # Commandes administrateur
│   ├── player/            # Commandes joueur
│   └── server/            # Commandes serveur
├── dataclass/             # Classes de données
│   ├── territory/         # Données des territoires (towns, regions)
│   ├── chunk/             # Données des chunks claimés
│   ├── property/          # Propriétés immobilières
│   └── newhistory/        # Historique des actions
├── economy/               # Système économique
│   ├── AbstractTanEcon.java
│   ├── TanEconomyVault.java
│   └── EconomyUtil.java
├── enums/                 # Énumérations
│   ├── TownRelation.java  # Relations diplomatiques
│   ├── RankEnum.java      # Rangs dans les villes
│   └── permissions/       # Types de permissions
├── events/                # Gestion des événements
│   ├── events/            # Événements internes
│   └── newsletter/        # Système de notifications
├── gui/                   # Interfaces graphiques
│   ├── user/              # GUIs utilisateurs
│   ├── admin/             # GUIs administrateur
│   ├── cosmetic/          # Éléments cosmétiques
│   └── legacy/            # GUIs dépréciées (à migrer)
├── lang/                  # Système de langues
│   ├── Lang.java          # Énumération des traductions
│   └── LangType.java      # Types de langues
├── listeners/             # Event listeners Bukkit/Folia
│   ├── chat/              # Listeners de chat
│   ├── interact/          # Listeners d'interaction
│   └── ChunkListener.java # Listener de chunks
├── storage/               # Persistance des données
│   ├── stored/            # Storages spécifiques
│   ├── database/          # Gestion SQL
│   └── blacklist/         # Listes noires
├── upgrade/               # Système d'améliorations
│   └── rewards/           # Récompenses d'upgrades
├── utils/                 # Utilitaires
│   ├── text/              # Utilitaires texte (ComponentUtil)
│   ├── territory/         # Utilitaires territoire
│   ├── gameplay/          # Utilitaires gameplay
│   └── deprecated/        # Code legacy (à nettoyer)
└── wars/                  # Système de guerre
    ├── PlannedAttack.java
    ├── War.java
    └── capture/           # Système de capture
```

### Principes d'organisation

1. **Séparation des responsabilités** : Chaque package a une responsabilité claire
2. **Modularité** : Les packages sont aussi indépendants que possible
3. **API vs Implémentation** : Séparation stricte entre API publique et implémentation
4. **Legacy code** : Code déprécié isolé dans `deprecated/` et `legacy/`

---

## API publique

### Vue d'ensemble

L'API publique (`tan-api`) permet aux développeurs externes d'interagir avec le plugin sans dépendre de l'implémentation interne.

### APIs disponibles

#### 1. EconomyAPI

Interface pour gérer l'économie des joueurs.

```java
EconomyAPI economyAPI = TANAPIProvider.getAPI().getEconomyAPI();

// Récupérer le solde
double balance = economyAPI.getBalance(player);

// Modifier le solde
economyAPI.addToBalance(player, 100.0);
economyAPI.removeFromBalance(player, 50.0);
economyAPI.setBalance(player, 1000.0);
```

#### 2. TownAPI

Interface pour gérer les villes.

```java
TownAPI townAPI = TANAPIProvider.getAPI().getTownAPI();

// Obtenir les informations d'une ville
Optional<TanTown> town = townAPI.getTown(townId);

// Vérifier si un joueur est dans une ville
boolean isInTown = townAPI.isPlayerInTown(player);
```

#### 3. NationAPI

Interface pour gérer les régions/nations.

```java
NationAPI nationAPI = TANAPIProvider.getAPI().getNationAPI();

// Obtenir une nation
Optional<TanNation> nation = nationAPI.getNation(nationId);
```

#### 4. ClaimAPI

Interface pour gérer les chunks claimés.

```java
ClaimAPI claimAPI = TANAPIProvider.getAPI().getClaimAPI();

// Vérifier si un chunk est claimé
boolean isClaimed = claimAPI.isChunkClaimed(chunk);

// Obtenir le propriétaire
Optional<TanClaimedChunk> claimedChunk = claimAPI.getClaimedChunk(chunk);
```

### Events personnalisés

Le plugin émet des événements personnalisés pour permettre aux développeurs de réagir aux actions :

```java
@EventHandler
public void onTownCreate(TownCreateEvent event) {
    TanTown town = event.getTown();
    Player creator = event.getPlayer();
    // Votre logique ici
}

@EventHandler
public void onTownJoin(TownJoinEvent event) {
    Player player = event.getPlayer();
    TanTown town = event.getTown();
    // Votre logique ici
}

@EventHandler
public void onWarDeclare(WarDeclareEvent event) {
    TanTown attacker = event.getAttacker();
    TanTown defender = event.getDefender();
    // Votre logique ici
}
```

### Utilisation de l'API

**build.gradle** :
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'io.github.leralix:tan-api:0.5.5'
}
```

**plugin.yml** :
```yaml
depend: [TownsAndNations]
```

---

## Système économique

### Architecture

Le système économique est structuré en trois couches :

1. **Interface API** (`EconomyAPI`) : API publique
2. **Couche d'abstraction** (`AbstractTanEcon`) : Logique commune
3. **Implémentations** :
   - `TanEconomyVault` : Intégration Vault
   - Implémentation interne du plugin

### Flux de données

```
Player Transaction Request
    ↓
EconomyAPI (Public Interface)
    ↓
AbstractTanEcon (Abstract Layer)
    ↓
TanEconomyVault (Vault Integration) ← → Vault API ← → Economy Plugin
    ↓
PlayerDataStorage (Database)
```

### Intégration Vault

Le plugin s'intègre avec Vault pour être compatible avec les autres plugins économiques :

```java
// Enregistrement du provider Vault
ServiceProvider<Economy> economyProvider =
    new ServiceProvider<>(Economy.class, new TanEconomyVault());
Bukkit.getServicesManager().register(
    Economy.class,
    economyProvider.getProvider(),
    plugin,
    ServicePriority.Normal
);
```

### Gestion des transactions

Les transactions sont gérées de manière thread-safe pour Folia :

```java
// Exemple de transaction sécurisée
public void transferMoney(Player from, Player to, double amount) {
    FoliaScheduler.runAsync(() -> {
        if (economyAPI.getBalance(from) >= amount) {
            economyAPI.removeFromBalance(from, amount);
            economyAPI.addToBalance(to, amount);
        }
    });
}
```

---

## Gestion des territoires

### Hiérarchie des territoires

```
Region (Nation)
    ├── Capital Town
    │   ├── Claimed Chunks
    │   ├── Properties
    │   └── Players
    ├── Vassal Town 1
    │   └── ...
    └── Vassal Town 2
        └── ...
```

### Classes principales

1. **TerritoryData** : Classe abstraite de base pour tous les territoires
2. **TownData** : Implémentation pour les villes
3. **RegionData** : Implémentation pour les régions/nations

### Système de relations

Les relations entre territoires sont gérées par l'énumération `TownRelation` :

```java
public enum TownRelation {
    SELF,           // Même ville
    OVERLORD,       // Seigneur
    VASSAL,         // Vassal
    ALLIANCE,       // Alliance
    NON_AGGRESSION, // Pacte de non-agression
    NEUTRAL,        // Neutre
    EMBARGO,        // Embargo
    WAR             // Guerre
}
```

### Permissions de chunks

Système de permissions granulaires pour chaque chunk :

```java
ChunkPermission permissions = chunk.getPermissions();

// Permissions par type de relation
permissions.setPermission(RelationPermission.ALLIANCE, ChunkPermissionType.BUILD, true);
permissions.setPermission(RelationPermission.FOREIGN, ChunkPermissionType.INTERACT, false);

// Permissions pour des joueurs spécifiques
permissions.addPlayerPermission(playerUUID, ChunkPermissionType.BUILD);
```

---

## Système de GUI

### Architecture des GUIs

Le plugin utilise la bibliothèque **Triumph-GUI** pour les interfaces graphiques.

### Structure

```
gui/
├── BasicGui.java           # Classe de base pour tous les GUIs
├── IteratorGUI.java        # GUI avec pagination
├── user/                   # GUIs utilisateurs
│   ├── MainMenu.java
│   ├── player/             # Menus joueur
│   ├── property/           # Menus propriétés
│   ├── territory/          # Menus territoire
│   └── war/                # Menus guerre
├── admin/                  # GUIs administrateur
│   └── AdminMainMenu.java
├── cosmetic/               # Éléments cosmétiques
│   ├── IconManager.java
│   └── type/               # Types d'icônes
└── legacy/                 # GUIs dépréciées
```

### Migration vers Adventure Components

Les GUIs ont été migrés pour utiliser l'Adventure API au lieu de ChatColor déprécié :

```java
// Ancien code (déprécié)
meta.setDisplayName(ChatColor.GREEN + "Nom");
meta.setLore(Arrays.asList("§aLigne 1", "§bLigne 2"));

// Nouveau code (Adventure)
ComponentUtil.setDisplayName(meta, "§aNom");
ComponentUtil.setLore(meta, Arrays.asList("§aLigne 1", "§bLigne 2"));
```

### Classe utilitaire ComponentUtil

La classe `ComponentUtil` facilite la conversion entre legacy et Adventure API :

```java
public class ComponentUtil {
    // Conversion legacy → Component
    Component component = ComponentUtil.fromLegacy("§aTexte coloré");

    // Conversion Component → legacy
    String legacy = ComponentUtil.toLegacy(component);

    // Helpers pour ItemMeta
    ComponentUtil.setDisplayName(itemMeta, "§aDisplay");
    ComponentUtil.setLore(itemMeta, loreList);
}
```

---

## Base de données et stockage

### Architecture de stockage

Le plugin utilise un système de stockage abstrait avec support SQL :

```
DatabaseStorage (Abstract)
    ├── PlayerDataStorage
    ├── TownDataStorage
    ├── RegionDataStorage
    ├── LandmarkStorage
    ├── PlannedAttackStorage
    └── WarStorage
```

### Bases de données supportées

1. **SQLite** (par défaut) : Base de données locale
2. **MySQL** : Base de données distante pour serveurs multi-instances

### Configuration

**config.yml** :
```yaml
database:
  type: sqlite  # ou mysql

  # Configuration MySQL (si type = mysql)
  mysql:
    host: localhost
    port: 3306
    database: townsandnations
    username: root
    password: password

  # Pool de connexions HikariCP
  hikari:
    maximum-pool-size: 10
    minimum-idle: 5
    connection-timeout: 30000
```

### Gestion asynchrone

Toutes les opérations de base de données sont asynchrones pour Folia :

```java
// Lecture asynchrone
CompletableFuture<TownData> townFuture =
    TownDataStorage.getInstance().get(townId);

townFuture.thenAccept(town -> {
    // Traitement sur le thread régional du joueur
    FoliaScheduler.runAtEntity(player, () -> {
        player.sendMessage("Ville : " + town.getName());
    });
});

// Écriture asynchrone
FoliaScheduler.runAsync(() -> {
    TownDataStorage.getInstance().save(townData);
});
```

### Migration de données

Le plugin inclut un système de migration pour les montées de version :

```java
public class DatabaseMigrationV2 implements DatabaseMigration {
    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public void migrate(Connection connection) throws SQLException {
        // Script de migration
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE towns ADD COLUMN description TEXT");
        }
    }
}
```

---

## Tests et qualité du code

### Structure des tests

```
src/test/java/
└── org/leralix/tan/
    ├── api/                    # Tests de l'API
    │   └── EconomyAPITest.java # Test exemple
    ├── commands/               # Tests des commandes
    ├── dataclass/              # Tests des dataclasses
    ├── listeners/              # Tests des listeners
    └── utils/                  # Tests des utilitaires
```

### Framework de tests

- **JUnit 5** : Framework de tests principal
- **Mockito** : Mocking et stubbing
- **MockBukkit** : Mock de l'API Bukkit/Paper

### Exemple de test

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("EconomyAPI Tests")
class EconomyAPITest {
    @Mock
    private EconomyAPI economyAPI;

    @Mock
    private Player player;

    @Test
    @DisplayName("Should get player balance")
    void testGetBalance() {
        // Arrange
        when(economyAPI.getBalance(player)).thenReturn(1000.0);

        // Act
        double balance = economyAPI.getBalance(player);

        // Assert
        assertEquals(1000.0, balance, 0.01);
        verify(economyAPI, times(1)).getBalance(player);
    }
}
```

### Couverture de code avec JaCoCo

Configuration dans `build.gradle` :

```gradle
jacoco {
    toolVersion = "0.8.11"
}

jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }

    // Exclusions
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/deprecated/**',
                '**/legacy/**',
                '**/gui/**',
                '**/lang/**'
            ])
        }))
    }
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.30  // 30% minimum
            }
        }
    }
}
```

### Commandes de test

```bash
# Exécuter tous les tests
./gradlew test

# Générer le rapport de couverture
./gradlew jacocoTestReport

# Vérifier la couverture minimale
./gradlew jacocoTestCoverageVerification

# Rapport HTML : build/reports/jacoco/test/html/index.html
```

### Qualité du code

#### Spotless (formatage automatique)

```bash
# Vérifier le formatage
./gradlew spotlessCheck

# Appliquer le formatage
./gradlew spotlessApply
```

Configuration :
- **Style** : Google Java Format
- **Imports** : Triés automatiquement
- **Fins de ligne** : Normalisées

---

## Migration Folia

### Changements principaux

#### 1. Scheduler régional

**Avant (Bukkit)** :
```java
Bukkit.getScheduler().runTask(plugin, () -> {
    // Code
});
```

**Après (Folia)** :
```java
FoliaScheduler.runAtEntity(entity, () -> {
    // Code exécuté sur le thread régional de l'entité
});

FoliaScheduler.runAsync(() -> {
    // Code asynchrone (I/O, database)
});
```

#### 2. API Adventure (remplacement ChatColor)

**Avant** :
```java
ChatColor.GREEN + "Texte"
```

**Après** :
```java
NamedTextColor.GREEN
// ou pour legacy support
ComponentUtil.fromLegacy("§aTexte")
```

#### 3. ItemMeta moderne

**Avant** :
```java
meta.setDisplayName(ChatColor.GREEN + "Nom");
meta.setLore(Arrays.asList("§aLigne"));
```

**Après** :
```java
ComponentUtil.setDisplayName(meta, "§aNom");
ComponentUtil.setLore(meta, Arrays.asList("§aLigne"));
```

#### 4. SignSide moderne

**Avant** :
```java
signSide.setLine(0, "Ligne 1");
```

**Après** :
```java
signSide.line(0, ComponentUtil.fromLegacy("Ligne 1"));
```

### Classe FoliaScheduler

Wrapper pour simplifier l'utilisation du scheduler Folia :

```java
public class FoliaScheduler {
    public static void runAtEntity(Entity entity, Runnable task) {
        entity.getScheduler().run(plugin, task -> task.run(), null);
    }

    public static void runAsync(Runnable task) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> task.run());
    }

    public static void runAtLocation(Location loc, Runnable task) {
        Bukkit.getRegionScheduler().run(plugin, loc, task -> task.run());
    }
}
```

### Thread-safety

**Règles importantes** :

1. **Données de joueur** : Accès sur le thread régional du joueur
2. **Données de monde** : Accès sur le thread régional du chunk
3. **Base de données** : Toujours asynchrone
4. **Collections partagées** : Utiliser ConcurrentHashMap

**Exemple** :
```java
// ✓ Correct
FoliaScheduler.runAtEntity(player, () -> {
    PlayerData data = PlayerDataStorage.getSync(player);
    player.sendMessage("Balance: " + data.getBalance());
});

// ✗ Incorrect (race condition)
PlayerData data = PlayerDataStorage.getSync(player);
FoliaScheduler.runAtEntity(player, () -> {
    player.sendMessage("Balance: " + data.getBalance()); // Données potentiellement périmées
});
```

---

## CI/CD avec GitHub Actions

### Workflow de build

Le workflow `.github/workflows/build.yml` automatise :

1. **Build** : Compilation avec Gradle
2. **Tests** : Exécution de tous les tests
3. **Qualité** : Vérification Spotless
4. **Artefacts** : Publication du JAR

```yaml
name: Build and Test

on:
  push:
    branches: [ main, master, develop ]
  pull_request:
    branches: [ main, master, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build with Gradle
        run: ./gradlew clean build
```

### Commandes de build locales

```bash
# Build complet
./gradlew clean build

# Build sans tests
./gradlew clean build -x test

# Build avec shadow JAR
./gradlew shadowJar

# Nettoyer
./gradlew clean
```

---

## Bonnes pratiques

### Code style

1. **Utiliser Spotless** pour le formatage automatique
2. **Javadoc** pour toutes les méthodes publiques de l'API
3. **Nommage descriptif** pour les variables et méthodes
4. **Éviter les nombres magiques** : utiliser des constantes

### Performance

1. **Opérations I/O asynchrones** : Database, fichiers
2. **Cache intelligent** : Éviter les requêtes répétées
3. **Lazy loading** : Charger les données à la demande
4. **Profiling régulier** : Identifier les goulots d'étranglement

### Sécurité

1. **Validation des entrées** : Toujours valider les inputs utilisateur
2. **Prepared statements** : Éviter les injections SQL
3. **Permissions** : Vérifier les permissions avant les actions
4. **Rate limiting** : Limiter les commandes abusives

### Maintenance

1. **Tests** : Écrire des tests pour le code critique
2. **Documentation** : Tenir à jour la documentation
3. **Versioning** : Suivre le semantic versioning
4. **Changelog** : Documenter tous les changements

---

## Ressources et références

### Documentation officielle

- [Folia API](https://papermc.io/software/folia)
- [Paper API](https://docs.papermc.io/)
- [Adventure API](https://docs.adventure.kyori.net/)
- [Gradle](https://docs.gradle.org/)
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)

### Dépendances principales

- **Folia** : `dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT`
- **Adventure** : Inclus dans Paper/Folia
- **Vault** : `com.github.MilkBowl:VaultAPI:1.7.1`
- **PlaceholderAPI** : `me.clip:placeholderapi:2.11.5`
- **WorldGuard** : `com.sk89q.worldguard:worldguard-bukkit:7.0.9`

### Contact et support

- **Issues** : [GitHub Issues](https://github.com/leralix/Towns-and-Nations/issues)
- **Wiki** : [GitHub Wiki](https://github.com/leralix/Towns-and-Nations/wiki)
- **Discord** : [Serveur Discord](lien_discord)

---

**Dernière mise à jour** : 2025-01-06
**Version du plugin** : 0.16.0
**Auteur** : Leralix
