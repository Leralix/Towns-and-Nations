package org.leralix.tan.listeners;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Smoker;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceInventory;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.service.PermissionService;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

public class ChunkListener implements Listener {

  private final PermissionService permissionService = new PermissionService();

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {

    Player player = event.getPlayer();
    Block breakedBlock = event.getBlock();
    Location loc = breakedBlock.getLocation();

    if (breakedBlock.hasMetadata("propertySign")) {
      event.setCancelled(true);
      return;
    }

    if (breakedBlock.hasMetadata("fortFlag")) {
      event.setCancelled(true);
      return;
    }

    try {
      boolean canDo =
          permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK).join();
      if (!canDo) {
        event.setCancelled(true);
      }
    } catch (Exception e) {
      event.setCancelled(true);
      org.leralix.tan.TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error checking block break permission: " + e.getMessage());
    }
  }

  @EventHandler
  public void onBucketFillEvent(PlayerBucketFillEvent event) {
    Player player = event.getPlayer();
    Location loc = event.getBlock().getLocation();

    try {
      boolean canDo =
          permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK).join();
      if (!canDo) {
        event.setCancelled(true);
      }
    } catch (Exception e) {
      event.setCancelled(true);
      org.leralix.tan.TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error checking bucket fill permission: " + e.getMessage());
    }
  }

  @EventHandler
  public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
    Player player = event.getPlayer();
    Location loc = event.getBlock().getLocation();

    try {
      boolean canDo =
          permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.PLACE_BLOCK).join();
      if (!canDo) {
        event.setCancelled(true);
      }
    } catch (Exception e) {
      event.setCancelled(true);
      org.leralix.tan.TownsAndNations.getPlugin()
          .getLogger()
          .warning("Error checking bucket empty permission: " + e.getMessage());
    }
  }

  @EventHandler
  public void onPlayerInteractEvent(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    if (block == null) return;

    BlockData blockData = block.getBlockData();
    Material materialType = block.getType();
    Material materialBlock = blockData.getMaterial();

    Location loc = block.getLocation();

    if (block.getType() == Material.OAK_SIGN) {
      Sign sign = (Sign) block.getState();
      if (sign.hasMetadata("propertySign")) {
        event.setCancelled(true);
        return;
      }
    }

    if (Tag.BUTTONS.isTagged(materialType) || materialBlock == Material.LEVER) {

      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, event.getPlayer(), ChunkPermissionType.INTERACT_BUTTON)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (materialBlock == Material.CHEST
        || materialBlock == Material.TRAPPED_CHEST
        || materialBlock == Material.BARREL
        || materialBlock == Material.HOPPER
        || materialBlock == Material.DISPENSER
        || materialBlock == Material.DROPPER
        || materialBlock == Material.BREWING_STAND
        || materialBlock == Material.SHULKER_BOX) {

      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_CHEST)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (Tag.DOORS.isTagged(materialType)
        || Tag.TRAPDOORS.isTagged(materialType)
        || Tag.FENCE_GATES.isTagged(materialType)) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_DOOR)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (Tag.CANDLES.isTagged(materialType)
        || Tag.CANDLE_CAKES.isTagged(materialType)
        || Tag.FLOWER_POTS.isTagged(materialType)
        || Tag.CAULDRONS.isTagged(materialType)
        || materialBlock == Material.COMPOSTER
        || Tag.ALL_SIGNS.isTagged(materialType)
        || materialBlock == Material.CHISELED_BOOKSHELF
        || Tag.CAMPFIRES.isTagged(materialType)
        || materialBlock == Material.BEACON) {

      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_DECORATIVE_BLOCK)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (materialBlock == Material.JUKEBOX || materialBlock == Material.NOTE_BLOCK) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_MUSIC_BLOCK)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (materialBlock == Material.REDSTONE_WIRE
        || materialBlock == Material.REPEATER
        || materialBlock == Material.COMPARATOR
        || materialBlock == Material.DAYLIGHT_DETECTOR) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_REDSTONE)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (event.getItem() != null && event.getItem().getType() == Material.BONE_MEAL) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.USE_BONE_MEAL)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK
        && event.getClickedBlock() != null
        && event.getClickedBlock().getType() == Material.SWEET_BERRY_BUSH) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_BERRIES)
                .join();
        if (!canDo) event.setCancelled(true);
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if ((event.getAction() == Action.RIGHT_CLICK_AIR
            || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        && player.getItemInHand().getType() == Material.OAK_BOAT) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_BOAT)
                .join();
        if (!canDo) event.setCancelled(true);
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if ((event.getAction() == Action.RIGHT_CLICK_AIR
            || event.getAction() == Action.RIGHT_CLICK_BLOCK)
        && (player.getItemInHand().getType() == Material.MINECART)) {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_MINECART)
                .join();
        if (!canDo) event.setCancelled(true);
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (event.getAction() == Action.PHYSICAL
        && (event.getClickedBlock().getType() == Material.FARMLAND)) {

      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onBlocPlaced(BlockPlaceEvent event) {

    Player player = event.getPlayer();
    Location loc = event.getBlock().getLocation();

    try {
      boolean canDo =
          permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.PLACE_BLOCK).join();
      if (!canDo) {
        event.setCancelled(true);
      }
    } catch (Exception e) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    if (event.getDamager() instanceof Player player) {
      Entity entity = event.getEntity();
      Location loc = entity.getLocation();

      if (entity instanceof Allay
          || entity instanceof Axolotl
          || entity instanceof Bat
          || entity instanceof Camel
          || entity instanceof Cat
          || entity instanceof Chicken
          || entity instanceof Cow
          || entity instanceof Donkey
          || entity instanceof Fox
          || entity instanceof Frog
          || entity instanceof Horse
          || entity instanceof Mule
          || entity instanceof Ocelot
          || entity instanceof Parrot
          || entity instanceof Pig
          || entity instanceof Rabbit
          || entity instanceof Sheep
          || entity instanceof SkeletonHorse
          || entity instanceof Sniffer
          || entity instanceof Snowman
          || entity instanceof Squid
          || entity instanceof Strider
          || entity instanceof Turtle
          || entity instanceof Villager
          || entity instanceof WanderingTrader
          || entity instanceof Fish
          || entity instanceof Bee
          || entity instanceof Dolphin
          || entity instanceof Goat
          || entity instanceof IronGolem
          || entity instanceof Llama
          || entity instanceof Panda
          || entity instanceof PolarBear
          || entity instanceof Wolf
          || entity instanceof ArmorStand) {
        try {
          boolean canDo =
              permissionService
                  .canPlayerDoAction(loc, player, ChunkPermissionType.ATTACK_PASSIVE_MOB)
                  .join();
          if (!canDo) {
            event.setCancelled(true);
          }
        } catch (Exception e) {
          event.setCancelled(true);
        }
      } else if (entity instanceof ItemFrame) {
        try {
          boolean canDo =
              permissionService
                  .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_ITEM_FRAME)
                  .join();
          if (!canDo) {
            event.setCancelled(true);
          }
        } catch (Exception e) {
          event.setCancelled(true);
        }
      } else if (entity instanceof EnderCrystal) {
        try {
          boolean canDo =
              permissionService
                  .canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK)
                  .join();
          if (!canDo) {
            event.setCancelled(true);
          }
        } catch (Exception e) {
          event.setCancelled(true);
        }
      } else if (entity instanceof Player player2
          && !permissionService.canPvpHappen(player, player2)) {
        event.setCancelled(true);
      }
    }

    if (event.getDamager() instanceof Projectile projectile) {

      if (projectile.getShooter() instanceof Player player) {
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();

        if (entity instanceof Allay
            || entity instanceof Axolotl
            || entity instanceof Bat
            || entity instanceof Camel
            || entity instanceof Cat
            || entity instanceof Chicken
            || entity instanceof Cow
            || entity instanceof Donkey
            || entity instanceof Fox
            || entity instanceof Frog
            || entity instanceof Horse
            || entity instanceof Mule
            || entity instanceof Ocelot
            || entity instanceof Parrot
            || entity instanceof Pig
            || entity instanceof Rabbit
            || entity instanceof Sheep
            || entity instanceof SkeletonHorse
            || entity instanceof Sniffer
            || entity instanceof Snowman
            || entity instanceof Squid
            || entity instanceof Strider
            || entity instanceof Turtle
            || entity instanceof Villager
            || entity instanceof WanderingTrader
            || entity instanceof Fish
            || entity instanceof Bee
            || entity instanceof Dolphin
            || entity instanceof Goat
            || entity instanceof IronGolem
            || entity instanceof Llama
            || entity instanceof Panda
            || entity instanceof PolarBear
            || entity instanceof Wolf
            || entity instanceof ArmorStand) {

          try {
            boolean canDo =
                permissionService
                    .canPlayerDoAction(loc, player, ChunkPermissionType.ATTACK_PASSIVE_MOB)
                    .join();
            if (!canDo) {
              event.setCancelled(true);
            }
          } catch (Exception e) {
            event.setCancelled(true);
          }
        } else if (entity instanceof ItemFrame) {
          try {
            boolean canDo =
                permissionService
                    .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_ITEM_FRAME)
                    .join();
            if (!canDo) {
              event.setCancelled(true);
            }
          } catch (Exception e) {
            event.setCancelled(true);
          }
        } else if (entity instanceof EnderCrystal) {
          try {
            boolean canDo =
                permissionService
                    .canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK)
                    .join();
            if (!canDo) {
              event.setCancelled(true);
            }
          } catch (Exception e) {
            event.setCancelled(true);
          }
        } else if (entity instanceof Player player2
            && !permissionService.canPvpHappen(player, player2)) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    if (event.getPlayer() instanceof Player player
        && (event.getInventory() instanceof FurnaceInventory
            || event.getInventory() instanceof BlastFurnace
            || event.getInventory() instanceof Smoker)) {

      Location loc = event.getInventory().getLocation();
      if (loc == null) return;
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_FURNACE)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

    Player player = event.getPlayer();

    if (event.getRightClicked() instanceof ItemFrame itemFrame) {
      Location loc = itemFrame.getLocation();

      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_ITEM_FRAME)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else if (event.getRightClicked() instanceof LeashHitch leashHitch) {
      Location loc = leashHitch.getLocation();

      try {
        boolean canDo =
            permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.USE_LEAD).join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }

    } else if (event.getRightClicked() instanceof LivingEntity livingEntity) {
      if (livingEntity.isLeashed()) {
        Location loc = livingEntity.getLocation();

        try {
          boolean canDo =
              permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.USE_LEAD).join();
          if (!canDo) {
            event.setCancelled(true);
          }
        } catch (Exception e) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
    if (event.getRightClicked() instanceof ArmorStand armorStand) {
      Player player = event.getPlayer();
      Location loc = armorStand.getLocation();

      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.INTERACT_ARMOR_STAND)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
    Player player = event.getPlayer();
    Entity entity = event.getEntity();
    Location loc = entity.getLocation();

    try {
      boolean canDo =
          permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.USE_LEAD).join();
      if (!canDo) {
        event.setCancelled(true);
      }
    } catch (Exception e) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
    Entity remover = event.getRemover();
    if (remover instanceof Player player) {
      Entity entity = event.getEntity();
      Location loc = entity.getLocation();

      if (entity instanceof LeashHitch) {
        try {
          boolean canDo =
              permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.USE_LEAD).join();
          if (!canDo) {
            event.setCancelled(true);
          }
        } catch (Exception e) {
          event.setCancelled(true);
        }
      } else {
        try {
          boolean canDo =
              permissionService
                  .canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK)
                  .join();
          if (!canDo) {
            event.setCancelled(true);
          }
        } catch (Exception e) {
          event.setCancelled(true);
        }
      }
    } else if (remover instanceof Projectile projectile) {
      if (projectile.getShooter() instanceof Player player) {
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();

        if (entity instanceof LeashHitch) {
          try {
            boolean canDo =
                permissionService
                    .canPlayerDoAction(loc, player, ChunkPermissionType.USE_LEAD)
                    .join();
            if (!canDo) {
              event.setCancelled(true);
            }
          } catch (Exception e) {
            event.setCancelled(true);
          }
        } else {
          try {
            boolean canDo =
                permissionService
                    .canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK)
                    .join();
            if (!canDo) {
              event.setCancelled(true);
            }
          } catch (Exception e) {
            event.setCancelled(true);
          }
        }
      }
    }
  }

  @EventHandler
  public void onHangingPlaceEvent(HangingPlaceEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();
    Location loc = block.getLocation();

    Entity entity = event.getEntity();
    if (entity instanceof LeashHitch) {
      try {
        boolean canDo =
            permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.USE_LEAD).join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    } else {
      try {
        boolean canDo =
            permissionService
                .canPlayerDoAction(loc, player, ChunkPermissionType.PLACE_BLOCK)
                .join();
        if (!canDo) {
          event.setCancelled(true);
        }
      } catch (Exception e) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onPlayerShearEntityEvent(PlayerShearEntityEvent event) {
    Player player = event.getPlayer();
    Location loc = event.getEntity().getLocation();

    try {
      boolean canDo =
          permissionService.canPlayerDoAction(loc, player, ChunkPermissionType.USE_SHEARS).join();
      if (!canDo) {
        event.setCancelled(true);
      }
    } catch (Exception e) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onExplosion(EntityExplodeEvent event) {
    event
        .blockList()
        .removeIf(
            block ->
                !NewClaimedChunkStorage.getInstance().get(block.getChunk()).canExplosionGrief());
  }

  @EventHandler
  public void onBurning(BlockBurnEvent event) {
    Chunk chunk = event.getBlock().getChunk();

    if (!NewClaimedChunkStorage.getInstance().get(chunk).canFireGrief()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onFireSpreading(BlockSpreadEvent event) {

    if (event.getSource().getType() == Material.FIRE) {
      Chunk chunk = event.getBlock().getChunk();
      if (!NewClaimedChunkStorage.getInstance().get(chunk).canFireGrief()) {
        event.setCancelled(true);
      }
    }
  }

  public void onWitherBlockBreak(EntityChangeBlockEvent event) {

    Chunk chunk = event.getBlock().getChunk();

    if (!NewClaimedChunkStorage.getInstance().get(chunk).canMobGrief()) {
      event.setCancelled(true);
    }
  }
}
