# Async GUI Migration Guide

## 📚 Why Migrate to Async?

### The Problem with Blocking GUIs

In Folia (multi-threaded Minecraft), blocking the thread while loading data causes:
- **Server lag** - The entire region freezes
- **Poor user experience** - GUI takes seconds to open
- **Crashes** - Potential deadlocks in Folia environments

### The Solution: Async Data Loading

Load data in background threads, then create GUI on the main thread with pre-fetched data.

---

## 🔴 Old Pattern (DEPRECATED)

```java
// ❌ DON'T DO THIS - Blocks the thread!
public class OldTownMenu extends BasicGui {

    public OldTownMenu(Player player) {
        super(player, Lang.TOWN_MENU_TITLE, 3);  // ← getSync() called here!
        // GUI is blocked for 50-200ms while fetching data
    }

    public static void openMenu(Player player) {
        OldTownMenu menu = new OldTownMenu(player);  // ← Blocks!
        menu.open();
    }
}
```

**Problems:**
- `super(player, Lang.TOWN_MENU_TITLE, 3)` calls deprecated constructor
- Deprecated constructor calls `PlayerDataStorage.getInstance().getSync(player)`
- `getSync()` blocks the thread waiting for database/cache

---

## ✅ New Pattern (RECOMMENDED)

```java
// ✅ CORRECT - Async data loading
public class NewTownMenu extends BasicGui {

    // Use constructor that accepts pre-fetched data
    public NewTownMenu(Player player, ITanPlayer tanPlayer, TownData town) {
        super(player, tanPlayer, Lang.TOWN_MENU_TITLE.get(tanPlayer), 3);
        // No blocking - data already loaded!
        populateMenu(town);
    }

    private void populateMenu(TownData town) {
        // Add GUI items using pre-loaded data
        // ...
    }

    // Static factory method with async loading
    public static void openMenu(Player player) {
        // Step 1: Load player data asynchronously
        PlayerDataStorage.getInstance()
            .get(player)
            .thenCompose(tanPlayer -> {
                // Step 2: Load town data asynchronously
                return TownDataStorage.getInstance()
                    .get(tanPlayer.getTownID())
                    .thenApply(town -> new Object[]{tanPlayer, town});
            })
            .thenAccept(data -> {
                ITanPlayer tanPlayer = (ITanPlayer) data[0];
                TownData town = (TownData) data[1];

                // Step 3: Create and open GUI on main thread
                FoliaScheduler.runTask(
                    TownsAndNations.getPlugin(),
                    player.getLocation(),
                    () -> {
                        NewTownMenu menu = new NewTownMenu(player, tanPlayer, town);
                        menu.open();
                    });
            })
            .exceptionally(throwable -> {
                player.sendMessage("§cError loading menu");
                TownsAndNations.getPlugin()
                    .getLogger()
                    .severe("Failed to open town menu: " + throwable.getMessage());
                return null;
            });
    }
}
```

**Benefits:**
- ✅ No thread blocking
- ✅ Folia-compatible
- ✅ Better error handling
- ✅ Faster perceived performance

---

## 📋 Step-by-Step Migration

### Step 1: Identify Deprecated Constructors

Search for:
```java
super(player, Lang.SOME_TITLE, rows)
super(player, "Title", rows)
```

### Step 2: Add Pre-fetching Constructor

```java
public class MyGui extends BasicGui {

    // OLD - Deprecated
    @Deprecated
    public MyGui(Player player) {
        super(player, Lang.TITLE, 3);
    }

    // NEW - Non-blocking
    public MyGui(Player player, ITanPlayer tanPlayer) {
        super(player, tanPlayer, Lang.TITLE.get(tanPlayer), 3);
    }
}
```

### Step 3: Create Async Factory Method

```java
public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(tanPlayer -> {
            FoliaScheduler.runTask(
                plugin,
                player.getLocation(),
                () -> {
                    MyGui gui = new MyGui(player, tanPlayer);
                    gui.open();
                });
        });
}
```

### Step 4: Update All Callers

```java
// OLD
MyGui gui = new MyGui(player);
gui.open();

// NEW
MyGui.open(player);
```

---

## 🎯 Common Patterns

### Pattern 1: Simple GUI (Player Data Only)

```java
public class PlayerStatsMenu extends BasicGui {

    public PlayerStatsMenu(Player player, ITanPlayer tanPlayer) {
        super(player, tanPlayer, "Your Stats", 3);
        // Display stats...
    }

    public static void open(Player player) {
        PlayerDataStorage.getInstance()
            .get(player)
            .thenAccept(tanPlayer ->
                FoliaScheduler.runTask(
                    plugin,
                    player.getLocation(),
                    () -> new PlayerStatsMenu(player, tanPlayer).open()));
    }
}
```

### Pattern 2: GUI with Multiple Data Sources

```java
public class TownMembersMenu extends BasicGui {

    public TownMembersMenu(Player player, ITanPlayer tanPlayer,
                           TownData town, List<ITanPlayer> members) {
        super(player, tanPlayer, "Town Members", 6);
        populateMembers(members);
    }

    public static void open(Player player) {
        PlayerDataStorage.getInstance()
            .get(player)
            .thenCompose(tanPlayer ->
                TownDataStorage.getInstance()
                    .get(tanPlayer.getTownID())
                    .thenCompose(town ->
                        loadAllMembers(town)
                            .thenApply(members ->
                                new Object[]{tanPlayer, town, members})))
            .thenAccept(data -> {
                ITanPlayer tanPlayer = (ITanPlayer) data[0];
                TownData town = (TownData) data[1];
                List<ITanPlayer> members = (List<ITanPlayer>) data[2];

                FoliaScheduler.runTask(
                    plugin,
                    player.getLocation(),
                    () -> new TownMembersMenu(player, tanPlayer, town, members).open());
            });
    }

    private static CompletableFuture<List<ITanPlayer>> loadAllMembers(TownData town) {
        // Load all member data asynchronously
        // ...
    }
}
```

### Pattern 3: Error Handling

```java
public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(tanPlayer -> {
            // Validate data
            if (tanPlayer.getTownID() == null) {
                player.sendMessage("§cYou are not in a town");
                return;
            }

            FoliaScheduler.runTask(
                plugin,
                player.getLocation(),
                () -> new MyGui(player, tanPlayer).open());
        })
        .exceptionally(throwable -> {
            player.sendMessage("§cFailed to load data");
            plugin.getLogger().severe("Error: " + throwable.getMessage());
            return null;
        });
}
```

---

## ⚡ Performance Tips

### 1. Batch Data Loading

```java
// ❌ BAD - Sequential loading
CompletableFuture<TownData> town = getTown();
CompletableFuture<List<Member>> members = town.thenCompose(t -> getMembers(t));

// ✅ GOOD - Parallel loading
CompletableFuture<TownData> townFuture = getTown();
CompletableFuture<RegionData> regionFuture = getRegion();

CompletableFuture.allOf(townFuture, regionFuture)
    .thenAccept(v -> {
        TownData town = townFuture.join();
        RegionData region = regionFuture.join();
        // Create GUI with both data sources
    });
```

### 2. Cache Pre-warmed Data

```java
// If data is already in cache, it returns immediately
PlayerDataStorage.getInstance().get(player)  // Fast if cached
```

### 3. Minimize Main Thread Work

```java
// ✅ Do heavy computation in async
PlayerDataStorage.getInstance()
    .get(player)
    .thenApply(tanPlayer -> {
        // Heavy calculation here (async)
        List<ProcessedData> processed = processData(tanPlayer);
        return new Object[]{tanPlayer, processed};
    })
    .thenAccept(data -> {
        // Only GUI creation on main thread
        FoliaScheduler.runTask(plugin, player.getLocation(), () -> {
            createGUI(data);  // Fast!
        });
    });
```

---

## 🧪 Testing Async GUIs

```java
@Test
void testAsyncGUICreation() {
    Player mockPlayer = mock(Player.class);
    ITanPlayer mockTanPlayer = mock(ITanPlayer.class);

    // Create GUI with pre-fetched data
    MyGui gui = new MyGui(mockPlayer, mockTanPlayer);

    assertNotNull(gui);
    // No async operations in constructor - easy to test!
}
```

---

## 📊 Migration Checklist

- [ ] Identify all GUI classes extending BasicGui
- [ ] Add non-blocking constructors with pre-fetched data
- [ ] Create static `open()` methods with async loading
- [ ] Update all callers to use `MyGui.open(player)`
- [ ] Mark old constructors as `@Deprecated`
- [ ] Test on Folia server
- [ ] Remove deprecated constructors after migration complete

---

## 🆘 Troubleshooting

### GUI Opens Slowly

**Cause**: Data not cached, loading from database.

**Solution**: Warm cache on player join:
```java
@EventHandler
public void onJoin(PlayerJoinEvent event) {
    PlayerDataStorage.getInstance().get(event.getPlayer()); // Warm cache
}
```

### NullPointerException in GUI

**Cause**: Data not loaded before GUI creation.

**Solution**: Always use `thenAccept()` to ensure data is ready:
```java
getData().thenAccept(data -> {
    if (data != null) {
        createGUI(data);
    }
});
```

### Thread Errors in Folia

**Cause**: GUI created on wrong thread.

**Solution**: Always use `FoliaScheduler.runTask()`:
```java
FoliaScheduler.runTask(plugin, player.getLocation(), () -> {
    gui.open();  // Must be on main thread
});
```

---

## 📚 Additional Resources

- [Folia Documentation](https://papermc.io/software/folia)
- [CompletableFuture Guide](https://www.baeldung.com/java-completablefuture)
- [BasicGui Class](tan-core/src/main/java/org/leralix/tan/gui/BasicGui.java)

---

**Last Updated**: 2025-01-08
**Version**: 0.16.0
**Maintainer**: Towns and Nations Development Team
