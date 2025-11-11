# Towns and Nations - Developer Guide

## 📚 Table of Contents

- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [API Usage](#api-usage)
- [Best Practices](#best-practices)
- [Common Patterns](#common-patterns)
- [Contributing](#contributing)
- [Testing](#testing)

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Gradle 8.14** or higher
- **Git**
- **IntelliJ IDEA** (recommended) or Eclipse

### Building the Project

```bash
# Clone the repository
git clone https://github.com/Leralix/Towns-and-Nations.git
cd Towns-and-Nations

# Build with Gradle
./gradlew build

# Run tests
./gradlew test

# Generate JAR
./gradlew shadowJar
```

The compiled JAR will be in `tan-core/build/libs/TownsAndNations-<version>.jar`

---

## 📁 Project Structure

```
Towns-and-Nations/
├── tan-api/                      # Public API module
│   └── src/main/java/org/tan/api/
│       ├── TownAPI.java          # Town management API
│       ├── EconomyAPI.java       # Economy API
│       ├── ClaimAPI.java         # Chunk claiming API
│       └── TANAPIProvider.java   # Main API entry point
│
├── tan-core/                     # Core implementation
│   └── src/main/java/org/leralix/tan/
│       ├── commands/             # Command handlers
│       ├── dataclass/            # Data models
│       │   ├── territory/        # Town/Region data
│       │   │   ├── components/   # NEW: Modular components
│       │   │   │   ├── TerritoryEconomy.java
│       │   │   │   ├── TerritoryChunks.java
│       │   │   │   └── TerritoryRelations.java
│       │   │   ├── TownData.java
│       │   │   └── RegionData.java
│       │   └── chunk/            # Chunk data
│       ├── economy/              # Economy system
│       ├── events/               # Custom events
│       ├── exception/            # NEW: Custom exceptions
│       ├── gui/                  # GUI menus
│       ├── listeners/            # Event listeners
│       ├── storage/              # Database & caching
│       ├── utils/                # Utilities
│       │   ├── CommandCooldownManager.java  # NEW: Rate limiting
│       │   └── FoliaScheduler.java          # Folia support
│       └── TownsAndNations.java  # Main plugin class
│
└── build.gradle                  # Root build configuration
```

---

## 🏗️ Architecture Overview

### Module Separation

**tan-api** (Public API):
- Contains only interfaces and data transfer objects
- No dependencies on implementation details
- Stable and backward-compatible
- Used by other plugins to integrate with TaN

**tan-core** (Implementation):
- Contains all business logic
- Database operations
- Event handling
- GUI management

### Design Patterns Used

#### 1. **Singleton Pattern**
```java
// Storage classes
PlayerDataStorage.getInstance()
TownDataStorage.getInstance()
```

#### 2. **Wrapper/Adapter Pattern**
```java
// API wrappers
TownDataWrapper implements TanTown
PropertyDataWrapper implements TanProperty
```

#### 3. **Component Pattern** (NEW in v0.16.0)
```java
// TerritoryData now delegates to components
class TerritoryData {
    private TerritoryEconomy economy;
    private TerritoryChunks chunks;
    private TerritoryRelations relations;
}
```

#### 4. **DAO Pattern**
```java
// Database access
DatabaseStorage<T extends Storable>
PlayerDataStorage extends DatabaseStorage<PlayerData>
```

#### 5. **Observer Pattern**
```java
// Custom events
EventManager.callEvent(new TownCreateEvent(town));
```

---

## 🔌 API Usage

### For Plugin Developers

#### 1. Add Dependency

**Maven:**
```xml
<dependency>
    <groupId>io.github.leralix</groupId>
    <artifactId>tan-api</artifactId>
    <version>0.5.5</version>
    <scope>provided</scope>
</dependency>
```

**Gradle:**
```gradle
dependencies {
    compileOnly 'io.github.leralix:tan-api:0.5.5'
}
```

#### 2. Access the API

```java
import org.tan.api.*;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Check if TaN is loaded
        if (Bukkit.getPluginManager().getPlugin("TownsAndNations") == null) {
            getLogger().severe("Towns and Nations not found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Use the API
        TownAPI townAPI = TANAPIProvider.getTownAPI();
        EconomyAPI economyAPI = TANAPIProvider.getEconomyAPI();
        ClaimAPI claimAPI = TANAPIProvider.getClaimAPI();
    }
}
```

#### 3. Example: Get Player's Town

```java
Player player = /* ... */;
TownAPI townAPI = TANAPIProvider.getTownAPI();

TanTown town = townAPI.getTown(player);
if (town != null) {
    player.sendMessage("You are in: " + town.getName());
    player.sendMessage("Balance: " + town.getBalance());
} else {
    player.sendMessage("You are not in a town");
}
```

#### 4. Example: Check Chunk Ownership

```java
Chunk chunk = player.getLocation().getChunk();
ClaimAPI claimAPI = TANAPIProvider.getClaimAPI();

TanClaimedChunk claimedChunk = claimAPI.getClaimedChunk(chunk);
if (claimedChunk != null) {
    player.sendMessage("This chunk is owned by: " + claimedChunk.getOwnerName());
} else {
    player.sendMessage("This chunk is wilderness");
}
```

---

## ✅ Best Practices

### 1. **Always Use Async Operations (Folia Compatibility)**

❌ **BAD** (Blocks thread):
```java
ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
tanPlayer.getTown(); // Blocks!
```

✅ **GOOD** (Async):
```java
PlayerDataStorage.getInstance()
    .get(player)
    .thenAccept(tanPlayer -> {
        // Work with data asynchronously
        TownData town = tanPlayer.getTownSync();

        // Schedule back to main thread if needed
        FoliaScheduler.runTask(plugin, player.getLocation(), () -> {
            player.sendMessage("Your town: " + town.getName());
        });
    });
```

### 2. **Use Exception Hierarchy**

✅ **NEW** (v0.16.0):
```java
try {
    territoryManager.createTown(player, name);
} catch (PermissionException e) {
    player.sendMessage("You don't have permission: " + e.getRequiredPermission());
} catch (TerritoryException e) {
    player.sendMessage("Cannot create town: " + e.getMessage());
} catch (EconomyException e) {
    player.sendMessage("Insufficient funds!");
}
```

### 3. **Implement Rate Limiting**

✅ **NEW** (v0.16.0):
```java
// In your command handler
if (CommandCooldownManager.getInstance().hasCooldown(player, "town_create")) {
    long remaining = CommandCooldownManager.getInstance()
        .getRemainingCooldown(player, "town_create");
    player.sendMessage("Please wait " + remaining + " seconds");
    return false;
}

// Execute command...
boolean success = createTown(player, townName);

if (success) {
    // Set 5-minute cooldown
    CommandCooldownManager.getInstance().setCooldown(player, "town_create", 300);
}
```

### 4. **Proper Error Logging**

❌ **BAD**:
```java
try {
    // ...
} catch (Exception e) {
    e.printStackTrace(); // DON'T DO THIS
}
```

✅ **GOOD**:
```java
try {
    // ...
} catch (SQLException e) {
    plugin.getLogger().log(Level.SEVERE, "Database error while saving town", e);
} catch (StorageException e) {
    plugin.getLogger().log(Level.WARNING, "Cache error", e);
}
```

### 5. **GUI Creation (Folia-Safe)**

✅ **RECOMMENDED**:
```java
// Step 1: Fetch data async
PlayerDataStorage.getInstance()
    .get(player)
    .thenAccept(tanPlayer -> {
        // Step 2: Create GUI on main thread with pre-fetched data
        FoliaScheduler.runTask(plugin, player.getLocation(), () -> {
            MyGui gui = new MyGui(player, tanPlayer, "Title", 3);
            gui.open();
        });
    });
```

---

## 🔄 Common Patterns

### Pattern 1: Safe Database Operation

```java
CompletableFuture<Void> saveTownAsync(TownData town) {
    return CompletableFuture.runAsync(() -> {
        try {
            TownDataStorage.getInstance().save(town);
        } catch (StorageException e) {
            logger.log(Level.SEVERE, "Failed to save town: " + town.getID(), e);
            throw new TanRuntimeException("Town save failed", e);
        }
    });
}
```

### Pattern 2: Event-Driven Updates

```java
@EventHandler
public void onTownCreate(TownCreateEvent event) {
    TownData town = event.getTown();
    ITanPlayer leader = event.getLeader();

    // Async processing
    CompletableFuture.runAsync(() -> {
        // Send notifications, update statistics, etc.
        broadcastTownCreation(town);
        updateLeaderboards();
    });
}
```

### Pattern 3: Permission Checking

```java
public void executeTownCommand(Player player, String action)
        throws PermissionException {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    TownData town = tanPlayer.getTownSync();

    if (town == null) {
        throw new TerritoryException("You are not in a town");
    }

    if (!town.hasPermission(tanPlayer, RolePermission.MANAGE_TOWN)) {
        throw new PermissionException(
            "You don't have permission to manage the town",
            "tan.town.manage"
        );
    }

    // Execute action...
}
```

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "org.leralix.tan.dataclass.territory.TownDataTest"

# Generate coverage report
./gradlew jacocoTestReport
# Report will be in: tan-core/build/reports/jacoco/test/html/index.html
```

### Writing Tests

```java
@Test
void testTownCreation() {
    // Arrange
    ITanPlayer leader = mock(ITanPlayer.class);
    when(leader.getID()).thenReturn("player-uuid");

    // Act
    TownData town = new TownData("town-id", "TestTown", leader);

    // Assert
    assertEquals("TestTown", town.getName());
    assertEquals(0.0, town.getBalance());
    assertNotNull(town.getEconomy());
}
```

### Test Coverage Goals

- **Target**: 60%+ line coverage
- **Current**: ~30% (work in progress)
- **Priority**: Core business logic (storage, economy, territories)

---

## 🤝 Contributing

### Code Style

- **Format**: Google Java Format (automatic via Spotless)
- **Line length**: 100 characters
- **Indentation**: 2 spaces
- **Imports**: Organized automatically

### Commit Guidelines

```bash
# Good commit messages:
git commit -m "feat: Add command cooldown system"
git commit -m "fix: Resolve territory economy null pointer"
git commit -m "refactor: Split TerritoryData into components"
git commit -m "docs: Update API usage examples"

# Conventional Commits format:
# type(scope): description
#
# Types: feat, fix, docs, style, refactor, test, chore
```

### Pull Request Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Make your changes
4. Run tests (`./gradlew test`)
5. Commit with clear messages
6. Push to your fork
7. Create a Pull Request

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/Leralix/Towns-and-Nations/issues)
- **Discord**: https://discord.gg/Q8gZSFUuzb
- **Wiki**: [GitHub Wiki](https://github.com/Leralix/Towns-and-Nations/wiki)

---

## 📄 License

Towns and Nations is licensed under [MIT License](LICENSE).

---

**Last Updated**: 2025-01-08
**Version**: 0.16.0
**Author**: Leralix
