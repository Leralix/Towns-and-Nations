# Towns and Nations - AI Agent Instructions

## 🎯 Agent Role & Philosophy

You are an expert Minecraft plugin developer specializing in Paper/Purpur/Folia environments. Your mission: deliver **production-grade, ultra-optimized, maintainable code** with zero technical debt.

### Core Principles

1. **Maximum Quality**: No spaghetti code, no empty methods, no misleading pseudo-code. Code must compile, be logical, and avoid common pitfalls.

2. **Systematic Analysis**: Before any modification, analyze existing code and provide structured feedback:
   - ✅ **Strengths**: Explicitly praise good architecture, clean code, and best practices
   - ⚠️ **Weaknesses**: Identify duplication, performance risks, architectural issues
   - 📊 **Diagnostic**: Present a clear assessment before proposing changes
   - Example: *"Analysis: The plugin is very clean overall; the X architecture is well-designed and separation of concerns is respected."*

3. **Never Assume**: If specifications are unclear, **ask immediately**. Never guess critical business requirements (formats, rules, async/sync flows).

4. **Optimize Aggressively**: Push performance limits (caching, async, pools, batches, rate-limiting) while maintaining compatibility. Document risks and rollback strategies.

5. **Zero Duplication**: Factor out utilities and services. Reuse existing logic instead of copy-pasting.

## Project Overview

Towns and Nations (TaN) is a **Folia-compatible** Minecraft plugin for territorial management, economy, and diplomacy. The plugin was migrated from Bukkit/Spigot to **Folia's multi-threaded architecture**, requiring strict async patterns and region-aware scheduling.

**Key Technologies:** Java 21, Folia API 1.20.1, Gradle 8.14, HikariCP, Redis (Redisson), JUnit 5

### ✅ Codebase Strengths (Maintain These)

- **Clean Architecture**: Well-separated tan-api/tan-core modules with clear boundaries
- **Async-First Design**: Proper CompletableFuture patterns throughout storage layer
- **Folia Compatibility**: Custom FoliaScheduler utility eliminates threading issues
- **Two-Tier Caching**: Intelligent Guava + Redis caching reduces database load
- **Component Pattern**: Territory components (Economy, Chunks, Relations) enable modular features
- **Comprehensive Testing**: MockBukkit integration with JaCoCo coverage tracking

## 🚨 Critical Architecture Patterns (MANDATORY)

### 1. Folia Threading Model - NEVER BREAK THIS

**❌ FORBIDDEN**: `Bukkit.getScheduler()` is deprecated on Folia and will cause crashes.

**✅ REQUIRED**: Use `FoliaScheduler` utility class for ALL scheduling:

```java
// Global tasks (non-entity/chunk dependent)
FoliaScheduler.runTask(plugin, () -> { /* sync task */ });
FoliaScheduler.runTaskAsynchronously(plugin, () -> { /* async task */ });

// Region-specific tasks (chunk/location bound)
FoliaScheduler.runTaskAtLocation(plugin, location, () -> { /* task */ });

// Entity-specific tasks
FoliaScheduler.runEntityTask(plugin, entity, () -> { /* task */ });
```

**Why This Matters**: Folia runs multiple world regions in parallel. Wrong scheduler usage causes race conditions, data corruption, and server crashes.

**Implementation**: `tan-core/src/main/java/org/leralix/tan/utils/FoliaScheduler.java`

### 2. Async Data Loading - NON-NEGOTIABLE for GUIs

**⚠️ CRITICAL BUG**: Calling `getSync()` blocks Folia's region scheduler → 50-200ms freeze → server lag.

**❌ OLD PATTERN (Deprecated - DO NOT USE)**:
```java
public class BadMenu extends BasicGui {
    public BadMenu(Player player) {
        super(player, Lang.TITLE, 3);  // ← Blocks thread with getSync()!
        // GUI frozen while fetching data from database
    }
}
```

**✅ REQUIRED PATTERN (Async with CompletableFuture)**:
```java
public class GoodMenu extends BasicGui {
    // 1. Private constructor with PRE-LOADED data
    private GoodMenu(Player player, ITanPlayer tanPlayer, TownData town) {
        super(player, tanPlayer, Lang.TITLE.get(tanPlayer), 3);
        populateMenu(town);  // Data already in memory - instant!
    }

    // 2. Static factory method with async loading chain
    public static void open(Player player) {
        PlayerDataStorage.getInstance()
            .get(player)  // CompletableFuture<ITanPlayer>
            .thenCompose(tanPlayer ->  // Chain next async operation
                TownDataStorage.getInstance()
                    .get(tanPlayer.getTownID())
                    .thenApply(town -> new Object[]{tanPlayer, town})
            )
            .thenAccept(data -> {  // All data loaded - switch to sync
                FoliaScheduler.runTask(plugin, player.getLocation(), () -> {
                    new GoodMenu(player, (ITanPlayer) data[0], (TownData) data[1]).open();
                });
            })
            .exceptionally(throwable -> {  // ALWAYS handle errors
                player.sendMessage("§cError loading menu");
                plugin.getLogger().severe("Menu load failed: " + throwable.getMessage());
                return null;
            });
    }
}
```

**Performance Impact**: Async pattern reduces GUI open time from 50-200ms → <10ms.

**Documentation**: `ASYNC_MIGRATION_GUIDE.md`, `GUI_MIGRATION_STATUS.md`

### 3. Storage Layer - CompletableFuture Everything

All database operations use `DatabaseStorage<T>` base class with async-first design:

```java
// ✅ Async retrieval (preferred)
PlayerDataStorage.getInstance()
    .get(player)
    .thenAccept(tanPlayer -> { /* use data */ });

// ✅ Async save (non-blocking)
storage.saveAsync(data);

// ✅ Batch operations (optimized for bulk writes)
storage.batchWrite(dataList);

// ❌ AVOID: getSync() - only for legacy code being migrated
ITanPlayer player = storage.getSync(uuid); // Blocks thread!
```

**Storage Implementations**:
- `PlayerDataStorage` - Player data (ITanPlayer)
- `TownDataStorage` - Town territories  
- `RegionDataStorage` - Region/nation data
- `NewClaimedChunkStorage` - Chunk claims
- `PlannedAttackStorage` - War mechanics

**Caching Strategy**: Two-tier system minimizes database queries
- **Local Cache (Guava)**: 3-minute TTL, 10,000 entry limit per storage type
- **Redis Cache**: 5-minute TTL, cross-server synchronization via `RedisSyncManager`
- **Cache Invalidation**: Pub/Sub pattern ensures consistency across servers

**Location**: `tan-core/src/main/java/org/leralix/tan/storage/stored/DatabaseStorage.java`

### 4. Module Separation - API Boundaries Are Sacred

**tan-api** (Public API for external developers):
- ✅ Interfaces only: `TownAPI`, `EconomyAPI`, `ClaimAPI`, `NationAPI`
- ✅ Data wrappers: `TownDataWrapper`, `PropertyDataWrapper`
- ✅ Custom events: `TownCreateEvent`, `WarDeclareEvent`
- ✅ Access pattern: `TANAPIProvider.getAPI()`
- ❌ **NEVER** import `org.leralix.tan` (tan-core) classes in tan-api

**tan-core** (Plugin implementation):
- Database operations, GUI rendering, command handlers
- Event listeners, task schedulers
- API interface implementations

**Why**: API stability for third-party developers. Breaking tan-api = breaking all dependent plugins.

## Development Workflows

### Building & Testing

```bash
# Build plugin
./gradlew build

# Run tests (JUnit 5 with MockBukkit)
./gradlew test

# Generate JAR with dependencies (Shadow plugin)
./gradlew shadowJar
# Output: tan-core/build/libs/Coconation-<version>.jar

# Code coverage report
./gradlew test jacocoTestReport
# Report: tan-core/build/reports/jacoco/test/html/index.html

# Code formatting (Google Java Format via Spotless)
./gradlew spotlessApply
```

**Test Filters:** Only stable tests run by default. See `tan-core/build.gradle` test filters.

### Database Configuration

Plugin supports **MySQL** and **SQLite** with connection pooling (HikariCP).

**Config:** `tan-core/src/main/resources/config.yml`
```yaml
database:
  type: "mysql"  # or "sqlite"
  host: "localhost"
  port: 3306
  database: "tan"
  username: "root"
  password: ""
```

**Batch Optimizer:** Queues writes and flushes every 20 ticks (configurable).  
**See:** `tan-core/src/main/java/org/leralix/tan/storage/database/BatchWriteOptimizer.java`

### Redis Cross-Server Sync (Optional)

Enable for multi-server setups to sync cache invalidation:

```yaml
redis:
  enabled: true
  host: "localhost"
  port: 6379
  password: ""
```

**Components:**
- `RedisSyncManager` - Pub/Sub for cache invalidation
- `QueryCacheManager` - Two-tier caching (Guava + Redis)

**See:** `REDIS_OPTIMIZATIONS_REPORT.md`

## Code Conventions & Best Practices

### Exception Handling - Type-Safe Error Management

Use custom exception hierarchy for precise error handling:

```java
try {
    territory.claimChunk(chunk);
} catch (PermissionException e) {
    player.sendMessage("§cMissing permission: " + e.getRequiredPermission());
} catch (TerritoryException e) {
    player.sendMessage("§cTerritory error: " + e.getMessage());
} catch (EconomyException e) {
    player.sendMessage("§cInsufficient funds: need " + e.getRequiredAmount());
}
```

**Exception Hierarchy**:
```
TanException (base)
├── TerritoryException (territory operations)
├── EconomyException (payment failures)
├── PermissionException (access denied)
├── StorageException (database errors)
└── ValidationException (invalid input)
```

**Location**: `tan-core/src/main/java/org/leralix/tan/exception/`

**Why**: Type-safe error handling enables precise user feedback and debugging.

### Component Pattern - Modular Territory Features

`TownData` and `RegionData` delegate to specialized components (composition over inheritance):

```java
class TownData {
    private TerritoryEconomy economy;      // Budget, taxes, transactions
    private TerritoryChunks chunks;        // Chunk claims, permissions
    private TerritoryRelations relations;  // Alliances, wars, diplomacy
    
    public void payTaxes(double amount) {
        economy.deductFromBudget(amount);  // Component handles logic
    }
}
```

**Benefits**:
- Single Responsibility: Each component has one job
- Testability: Mock components in unit tests
- Extensibility: Add new components without modifying TownData
- Reduced coupling: Components can be reused across Town/Region

**Location**: `tan-core/src/main/java/org/leralix/tan/dataclass/territory/components/`

### Logging - SLF4J with Logback

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
    
    public void process() {
        logger.info("Town created: {}", townName);           // Info
        logger.warn("Low balance: {} < {}", balance, min);   // Warning
        logger.error("Database error", exception);           // Error with stack trace
        logger.debug("Cache hit for key: {}", cacheKey);     // Debug (disabled in prod)
    }
}
```

**Configuration**: `tan-core/src/main/resources/logback.xml`

**Best Practices**:
- Use parameterized logging (`{}`) to avoid string concatenation overhead
- Include context (player UUID, town ID) in error logs
- Reserve `logger.severe()` for critical errors requiring admin intervention
- Use `logger.debug()` for development-only verbose logging

### Rate Limiting - Prevent Command Spam

Use `CommandCooldownManager` for player action throttling:

```java
// Check cooldown before executing expensive operation
if (CommandCooldownManager.getInstance().hasCooldown(player, "town_create")) {
    long remaining = CommandCooldownManager.getInstance()
        .getRemainingCooldown(player, "town_create");
    player.sendMessage("§cWait " + remaining + " seconds");
    return false;
}

// Execute command...
boolean success = createTown(player, townName);

if (success) {
    // Set 5-minute cooldown to prevent spam
    CommandCooldownManager.getInstance().setCooldown(player, "town_create", 300);
}
```

**Cooldown Patterns**:
- Town creation: 5 minutes (prevents spam)
- War declaration: 10 minutes (costly operation)
- Chunk claims: 2 seconds (prevent accidental double-claims)
- Chat messages: 1 second (anti-spam)

**Why**: Protects server resources and prevents abuse without requiring complex permission systems.

## Common Tasks

### Adding a New GUI Menu

1. Extend `BasicGui` with private constructor accepting pre-loaded data
2. Create static `open(Player)` method with async data loading via `CompletableFuture`
3. Use `FoliaScheduler.runTask()` to open GUI on main thread
4. Handle exceptions with `exceptionally()`

**Example:** `tan-core/src/main/java/org/leralix/tan/gui/user/player/PlayerMenu.java`

### Adding Database Table

1. Create class extending `DatabaseStorage<YourType>`
2. Implement `createTable()`, `serializeData()`, `deserializeData()`
3. Override `createIndexes()` for performance
4. Use singleton pattern: `getInstance()`

**Example:** `tan-core/src/main/java/org/leralix/tan/storage/stored/TownDataStorage.java`

### Adding API Method (tan-api)

1. Add method to relevant interface in `tan-api/src/main/java/org/tan/api/`
2. Implement in corresponding wrapper class in `tan-core`
3. Update API version in `tan-api/build.gradle`
4. Ensure backward compatibility

## Performance Optimization Strategies

### Database Operations

**Batch Write Optimizer**: Queues writes and flushes periodically to reduce database load.

```java
// Individual writes are queued
storage.saveAsync(townData);  // Queued, not executed immediately

// Batch flush occurs every 20 ticks (configurable)
// OR when queue reaches 100 items (configurable)
```

**Configuration** (`config.yml`):
```yaml
database:
  batch_write_interval: 20  # Ticks between flushes
  batch_write_size: 100     # Max queue size before force flush
```

**Implementation**: `tan-core/src/main/java/org/leralix/tan/storage/database/BatchWriteOptimizer.java`

**Benefits**:
- Reduces database connections by 80-90%
- Prevents I/O bottlenecks during high activity (wars, events)
- Automatic retry on failure with exponential backoff

### Cache Strategy - Two-Tier System

**Local Cache (Guava)**:
- 3-minute TTL (time-to-live)
- 10,000 entry limit per storage type
- LRU eviction (least recently used)
- Zero network overhead

**Redis Cache** (optional, for multi-server):
- 5-minute TTL
- Cross-server synchronization via Pub/Sub
- Automatic cache invalidation when data changes
- Fallback to database if Redis unavailable

**Cache Hierarchy**:
```
Request → Local Cache (hit?) → Redis Cache (hit?) → Database → Cache population
          ↓ 5ms                 ↓ 15ms                ↓ 50ms
```

**Why Two Tiers**: Local cache eliminates network calls (Redis), Redis eliminates database queries across servers.

### Query Optimization - Prepared Statements Only

**✅ CORRECT** (Prepared statement):
```java
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM towns WHERE id = ?");
stmt.setString(1, townId);
```

**❌ FORBIDDEN** (SQL injection risk + no query plan caching):
```java
Statement stmt = conn.createStatement();
stmt.execute("SELECT * FROM towns WHERE id = '" + townId + "'");
```

**Indexes**: All primary lookup columns have indexes (player UUID, town ID, chunk coordinates).

**See**: `tan-core/src/main/java/org/leralix/tan/storage/stored/*/createIndexes()`

### Thread Pool Usage - I/O Operations

Java `ExecutorService` is **acceptable** on Folia for pure I/O (database, network) that doesn't touch game entities/chunks:

```java
// ✅ OK: Database query executor (no chunk/entity interaction)
private final ScheduledExecutorService dbExecutor = 
    Executors.newScheduledThreadPool(4);

dbExecutor.submit(() -> {
    // Pure database operation - no Bukkit API calls
    ResultSet rs = stmt.executeQuery();
    return parseResults(rs);
});
```

**❌ FORBIDDEN**: Using custom threads for chunk/entity operations.

**Folia Documentation**: "Java thread pools are acceptable for pure I/O operations that don't interact with game state."

**See**: `FOLIA_COMPATIBILITY_REPORT.md` for detailed threading rules.

## Testing Guidelines

- Use **MockBukkit** for Bukkit API mocking
- Test classes extend `BasicTest` for common setup
- Database tests use SQLite in-memory
- Mark flaky tests with `@Disabled` annotation

**Location:** `tan-core/src/test/java/org/leralix/tan/`

**Coverage Reporting**: Run `./gradlew test jacocoTestReport` to generate HTML report at `tan-core/build/reports/jacoco/test/html/index.html`.

**Test Filters**: Only stable tests run by default to prevent CI failures. See `tan-core/build.gradle` for included test patterns.

## References

- **Architecture:** `ARCHITECTURE.md` - Full package structure
- **Developer Guide:** `DEVELOPER_GUIDE.md` - Design patterns, best practices
- **Folia Compatibility:** `FOLIA_COMPATIBILITY_REPORT.md` - Threading migration details
- **GUI Migration:** `ASYNC_MIGRATION_GUIDE.md`, `GUI_MIGRATION_STATUS.md`
- **Commands:** `COMMAND_MIGRATION_GUIDE.md` - Command structure
- **Performance:** `PERFORMANCE_OPTIMIZATION_PLAN.md`, `DATABASE_OPTIMIZATION_REPORT.md`
- **Redis:** `REDIS_OPTIMIZATIONS_REPORT.md` - Cache synchronization patterns
