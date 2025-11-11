# Command Exception Handling Migration Guide

## 📚 Overview

This guide shows how to migrate commands from blocking `getSync()` calls to async pattern with typed exception handling.

**Target**: 36 command files
**Pattern**: Async CompletableFuture + try-catch with typed exceptions

---

## ❌ Old Pattern (Problematic)

```java
@Override
public void perform(Player player, String[] args) {
    LangType langType = LangType.of(player);

    // ❌ Blocking call
    TerritoryData territory = TownDataStorage.getInstance().getSync(player);

    // ❌ Manual null checks
    if (territory == null) {
        TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get());
        return;
    }

    // ❌ No permission checking
    territory.claimChunk(player);
}
```

**Problems**:
- Blocks thread for 50-200ms
- Manual error handling (if/null checks)
- No typed exceptions
- Not Folia-compatible
- Poor error messages

---

## ✅ New Pattern (Recommended)

```java
@Override
public void perform(Player player, String[] args) {
    // Step 1: Parse arguments early (fail fast)
    if (args.length != 2) {
        player.sendMessage(Lang.SYNTAX_ERROR.get(player));
        return;
    }

    String territoryType = args[1];

    // Step 2: Load data asynchronously
    PlayerDataStorage.getInstance()
        .get(player)
        .thenCompose(tanPlayer -> {
            if (territoryType.equals("town")) {
                return TownDataStorage.getInstance()
                    .get(tanPlayer.getTownID())
                    .thenApply(town -> (TerritoryData) town);
            } else {
                return RegionDataStorage.getInstance()
                    .get(tanPlayer.getRegionID())
                    .thenApply(region -> (TerritoryData) region);
            }
        })
        .thenAccept(territory -> {
            // Step 3: Execute on main thread with exception handling
            FoliaScheduler.runTask(
                TownsAndNations.getPlugin(),
                player.getLocation(),
                () -> {
                    try {
                        executeClaimLogic(player, territory);
                    } catch (TerritoryException e) {
                        player.sendMessage(Lang.TERRITORY_ERROR.get(player, e.getMessage()));
                    } catch (PermissionException e) {
                        player.sendMessage(Lang.NO_PERMISSION.get(player, e.getRequiredPermission()));
                    } catch (EconomyException e) {
                        player.sendMessage(Lang.NOT_ENOUGH_MONEY.get(player));
                    }
                });
        })
        .exceptionally(throwable -> {
            // Step 4: Handle async errors
            player.sendMessage(Lang.COMMAND_ERROR.get(player));
            TownsAndNations.getPlugin()
                .getLogger()
                .severe("Failed to execute claim command: " + throwable.getMessage());
            return null;
        });
}

/**
 * Executes claim logic with typed exceptions.
 *
 * @throws TerritoryException if territory is null or invalid
 * @throws PermissionException if player lacks permission
 * @throws EconomyException if insufficient funds
 */
private void executeClaimLogic(Player player, TerritoryData territory)
    throws TerritoryException, PermissionException, EconomyException {

    // Validation with typed exceptions
    if (territory == null) {
        throw new TerritoryException("You are not in a territory");
    }

    if (!territory.hasPermission(player, ChunkPermissionType.CLAIM)) {
        throw new PermissionException(
            "You don't have permission to claim chunks",
            "tan.territory.claim"
        );
    }

    double claimCost = territory.getChunkClaimCost();
    if (territory.getBalance() < claimCost) {
        throw new EconomyException("Territory treasury has insufficient funds");
    }

    // Execute claim
    territory.claimChunk(player);
    player.sendMessage(Lang.CHUNK_CLAIMED_SUCCESS.get(player));
}
```

**Benefits**:
- ✅ Non-blocking async pattern
- ✅ Typed exception handling
- ✅ Clear error messages
- ✅ Folia-compatible
- ✅ Testable business logic

---

## 🔄 Migration Steps

### Step 1: Identify Command Type

**Category A**: Simple Commands (Player data only)
- SeeBalanceCommand
- SetTownSpawnCommand
- ChannelChatScopeCommand

**Category B**: Territory Commands (Town/Region data)
- ClaimCommand ⭐ (example below)
- UnclaimCommand
- InvitePlayerCommand

**Category C**: Complex Commands (Multiple data sources)
- PayCommand (sender + receiver)
- JoinTownCommand (player + town)

---

### Step 2: Create Async Wrapper

```java
// Old synchronous method
@Override
public void perform(Player player, String[] args) {
    // Blocking logic here
}

// New async wrapper
@Override
public void perform(Player player, String[] args) {
    performAsync(player, args);
}

private void performAsync(Player player, String[] args) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(tanPlayer -> {
            FoliaScheduler.runTask(
                plugin,
                player.getLocation(),
                () -> executeWithExceptions(player, tanPlayer, args)
            );
        })
        .exceptionally(this::handleAsyncError);
}
```

---

### Step 3: Extract Business Logic

```java
/**
 * Business logic with typed exceptions.
 * Separated from async wrapper for testability.
 */
private void executeWithExceptions(Player player, ITanPlayer tanPlayer, String[] args)
    throws TerritoryException, PermissionException, EconomyException {

    // All validation and business logic here
    // Throw typed exceptions instead of returning early
}
```

---

### Step 4: Add Exception Handlers

```java
private void handleAsyncError(Throwable throwable) {
    if (throwable instanceof TerritoryException) {
        player.sendMessage(Lang.TERRITORY_ERROR.get(...));
    } else if (throwable instanceof PermissionException) {
        player.sendMessage(Lang.NO_PERMISSION.get(...));
    } else if (throwable instanceof EconomyException) {
        player.sendMessage(Lang.NOT_ENOUGH_MONEY.get(...));
    } else {
        player.sendMessage(Lang.GENERIC_ERROR.get(...));
        logger.severe("Unexpected error: " + throwable.getMessage());
    }
    return null;
}
```

---

## 📋 Complete Example: ClaimCommand

### Before (Old Pattern)

```java
package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.TownDataStorage;

public class ClaimCommand extends PlayerSubCommand {
    @Override
    public void perform(Player player, String[] args) {
        // ❌ Blocking
        TerritoryData territory = TownDataStorage.getInstance().getSync(player);

        // ❌ Manual null check
        if (territory == null) {
            player.sendMessage("§cYou are not in a town");
            return;
        }

        // ❌ No exception handling
        territory.claimChunk(player);
    }
}
```

---

### After (New Pattern)

```java
package org.leralix.tan.commands.player;

import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.exception.EconomyException;
import org.leralix.tan.exception.PermissionException;
import org.leralix.tan.exception.TerritoryException;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.FoliaScheduler;

/**
 * Claim command with async pattern and typed exception handling.
 *
 * @since 0.16.0
 */
public class ClaimCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return Lang.CLAIM_CHUNK_COMMAND_DESC.getDefault();
    }

    @Override
    public String getSyntax() {
        return "/tan claim <town/region>";
    }

    @Override
    public void perform(Player player, String[] args) {
        // Step 1: Validate arguments early
        if (args.length < 2) {
            player.sendMessage(Lang.SYNTAX_ERROR.get(player));
            player.sendMessage(Lang.CORRECT_SYNTAX.get(player, getSyntax()));
            return;
        }

        String territoryType = args[1].toLowerCase();
        if (!territoryType.equals("town") && !territoryType.equals("region")) {
            player.sendMessage(Lang.INVALID_TERRITORY_TYPE.get(player));
            return;
        }

        // Step 2: Execute async
        performAsync(player, territoryType);
    }

    /**
     * Async execution with data loading.
     */
    private void performAsync(Player player, String territoryType) {
        PlayerDataStorage.getInstance()
            .get(player)
            .thenCompose(tanPlayer -> loadTerritory(tanPlayer, territoryType))
            .thenAccept(data -> {
                ITanPlayer tanPlayer = (ITanPlayer) data[0];
                TerritoryData territory = (TerritoryData) data[1];

                // Step 3: Execute on main thread
                FoliaScheduler.runTask(
                    TownsAndNations.getPlugin(),
                    player.getLocation(),
                    () -> {
                        try {
                            executeClaimLogic(player, tanPlayer, territory);
                        } catch (TerritoryException e) {
                            player.sendMessage(
                                Lang.TERRITORY_ERROR.get(player, e.getMessage()));
                        } catch (PermissionException e) {
                            player.sendMessage(
                                Lang.NO_PERMISSION.get(player, e.getRequiredPermission()));
                        } catch (EconomyException e) {
                            player.sendMessage(
                                Lang.NOT_ENOUGH_MONEY.get(player));
                        } catch (Exception e) {
                            player.sendMessage(Lang.GENERIC_ERROR.get(player));
                            TownsAndNations.getPlugin()
                                .getLogger()
                                .severe("Unexpected error in claim command: " + e.getMessage());
                        }
                    });
            })
            .exceptionally(throwable -> {
                player.sendMessage(Lang.DATA_LOAD_ERROR.get(player));
                TownsAndNations.getPlugin()
                    .getLogger()
                    .severe("Failed to load data for claim command: " + throwable.getMessage());
                return null;
            });
    }

    /**
     * Loads territory data based on type.
     */
    private CompletableFuture<Object[]> loadTerritory(ITanPlayer tanPlayer, String type) {
        if (type.equals("town")) {
            return TownDataStorage.getInstance()
                .get(tanPlayer.getTownID())
                .thenApply(town -> new Object[]{tanPlayer, town});
        } else {
            return RegionDataStorage.getInstance()
                .get(tanPlayer.getRegionID())
                .thenApply(region -> new Object[]{tanPlayer, region});
        }
    }

    /**
     * Business logic with typed exceptions.
     *
     * @throws TerritoryException if territory validation fails
     * @throws PermissionException if player lacks permission
     * @throws EconomyException if insufficient funds
     */
    private void executeClaimLogic(Player player, ITanPlayer tanPlayer, TerritoryData territory)
        throws TerritoryException, PermissionException, EconomyException {

        // Validation
        if (territory == null) {
            throw new TerritoryException("You are not in a territory");
        }

        if (!territory.hasPermission(tanPlayer, ChunkPermissionType.CLAIM)) {
            throw new PermissionException(
                "You don't have permission to claim chunks in this territory",
                "tan.territory.claim"
            );
        }

        double claimCost = territory.getChunkClaimCost();
        if (territory.getBalance() < claimCost) {
            throw new EconomyException(
                "Territory treasury has insufficient funds. Cost: " + claimCost);
        }

        // Execute claim
        territory.claimChunk(player);

        // Success message
        player.sendMessage(
            Lang.CHUNK_CLAIMED_SUCCESS.get(
                tanPlayer,
                territory.getName(),
                String.valueOf(territory.getNumberOfClaimedChunk())
            ));
    }
}
```

---

## 📊 Migration Checklist

For each command file:

- [ ] Add async imports (CompletableFuture, FoliaScheduler)
- [ ] Add exception imports (TerritoryException, etc.)
- [ ] Create `performAsync()` method
- [ ] Extract business logic to separate method with exceptions
- [ ] Replace `getSync()` with `get().thenAccept()`
- [ ] Add typed exception handling (try-catch)
- [ ] Add async error handler (exceptionally)
- [ ] Update error messages to use Lang constants
- [ ] Test in-game

---

## 🎯 Priority Order

### High Priority (Core Functionality)
1. ClaimCommand
2. UnclaimCommand
3. InvitePlayerCommand
4. JoinTownCommand
5. PayCommand

### Medium Priority (Frequent Use)
6. TownSpawnCommand
7. SetTownSpawnCommand
8. OpenGuiCommand
9. MapCommand
10. AutoClaimCommand

### Low Priority (Admin/Debug)
11. ReloadCommand
12. UnclaimAdminCommand
13. LogLevelCommand

---

## ⚠️ Common Pitfalls

### Pitfall 1: Forgetting FoliaScheduler

```java
// ❌ BAD - Runs on async thread
.thenAccept(data -> {
    territory.claimChunk(player);  // NOT thread-safe!
});

// ✅ GOOD - Runs on main thread
.thenAccept(data -> {
    FoliaScheduler.runTask(plugin, player.getLocation(), () -> {
        territory.claimChunk(player);  // Thread-safe
    });
});
```

---

### Pitfall 2: Not Handling Null CompletableFuture

```java
// ❌ BAD - Can throw NullPointerException
return TownDataStorage.getInstance().get(null);

// ✅ GOOD - Return CompletedFuture with null
if (townId == null) {
    return CompletableFuture.completedFuture(null);
}
return TownDataStorage.getInstance().get(townId);
```

---

### Pitfall 3: Swallowing Exceptions

```java
// ❌ BAD - Silent failure
.exceptionally(throwable -> {
    return null;  // User never knows what happened
});

// ✅ GOOD - Log and notify
.exceptionally(throwable -> {
    player.sendMessage(Lang.ERROR.get(player));
    logger.severe("Error: " + throwable.getMessage());
    return null;
});
```

---

## 📚 Additional Resources

- [TanException.java](../tan-core/src/main/java/org/leralix/tan/exception/TanException.java)
- [CommandCooldownManager.java](../tan-core/src/main/java/org/leralix/tan/utils/CommandCooldownManager.java)
- [ASYNC_MIGRATION_GUIDE.md](ASYNC_MIGRATION_GUIDE.md)

---

**Last Updated**: 2025-01-08
**Version**: 0.16.0
**Status**: 0/36 commands migrated
