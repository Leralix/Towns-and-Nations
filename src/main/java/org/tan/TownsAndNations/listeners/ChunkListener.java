package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Smoker;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceInventory;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.TownChunkPermission;
import org.tan.TownsAndNations.enums.TownChunkPermissionType;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.storage.WarTaggedPlayer;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.BREAK))){
            event.setCancelled(true);
        };
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.BREAK))){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.PLACE))){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event){

        Block block = event.getClickedBlock();

        if (block != null){

            BlockData blockData = block.getBlockData();
            Material materialType = block.getType();
            Material materialBlock = blockData.getMaterial();

            Chunk chunk = block.getLocation().getChunk();
            //Player player = event.getPlayer();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;
            TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

            if(
                Tag.BUTTONS.isTagged(materialType) ||
                materialBlock == Material.LEVER
            ){
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.USE_BUTTONS))){
                    event.setCancelled(true);
                }
            }


            if(
                materialBlock == Material.CHEST ||
                materialBlock == Material.TRAPPED_CHEST ||
                materialBlock == Material.BARREL ||
                materialBlock == Material.HOPPER ||
                materialBlock == Material.DISPENSER ||
                materialBlock == Material.DROPPER ||
                materialBlock == Material.BREWING_STAND
            ){
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.CHEST))){
                    event.setCancelled(true);
                }
            }
            else if(
                    Tag.DOORS.isTagged(materialType) ||
                    Tag.TRAPDOORS.isTagged(materialType) ||
                    Tag.FENCE_GATES.isTagged(materialType)

            ){
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.DOOR))){
                    event.setCancelled(true);
                };
            }
            else if (

                    Tag.CANDLES.isTagged(materialType) ||
                    Tag.CANDLE_CAKES.isTagged(materialType) ||
                    Tag.FLOWER_POTS.isTagged(materialType) ||
                    Tag.CAULDRONS.isTagged(materialType) ||
                    materialBlock == Material.COMPOSTER ||
                    Tag.ALL_SIGNS.isTagged(materialType) ||
                    materialBlock == Material.CHISELED_BOOKSHELF ||
                    Tag.CAMPFIRES.isTagged(materialType) ||

                    materialBlock == Material.BEACON

            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.DECORATIVE_BLOCK))){
                    event.setCancelled(true);
                }
            }
            else if (
                    materialBlock == Material.JUKEBOX ||
                    materialBlock == Material.NOTE_BLOCK
            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.MUSIC_BLOCK))){
                    event.setCancelled(true);
                }
            }
            else if (
                    materialBlock == Material.REDSTONE_WIRE ||
                    materialBlock == Material.REPEATER ||
                    materialBlock == Material.COMPARATOR ||
                    materialBlock == Material.DAYLIGHT_DETECTOR
            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.USE_REDSTONE))){
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void OnBlocPlaced(BlockPlaceEvent event){

        Block block = event.getBlock();
        Chunk chunk = block.getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.PLACE))){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player player) {
            Entity entity = event.getEntity();


            if(
                entity instanceof Allay ||
                entity instanceof Axolotl ||
                entity instanceof Bat ||
                entity instanceof Camel ||
                entity instanceof Cat ||
                entity instanceof Chicken ||
                entity instanceof Cow ||
                entity instanceof Donkey ||
                entity instanceof Fox ||
                entity instanceof Frog ||
                entity instanceof Horse ||
                entity instanceof Mule ||
                entity instanceof Ocelot ||
                entity instanceof Parrot ||
                entity instanceof Pig ||
                entity instanceof Rabbit ||
                entity instanceof Sheep ||
                entity instanceof SkeletonHorse ||
                entity instanceof Sniffer ||
                entity instanceof Snowman ||
                entity instanceof Squid ||
                entity instanceof Strider ||
                entity instanceof Turtle ||
                entity instanceof Villager ||
                entity instanceof WanderingTrader ||
                entity instanceof Fish ||
                entity instanceof Bee ||
                entity instanceof Dolphin ||
                entity instanceof Goat ||
                entity instanceof IronGolem ||
                entity instanceof Llama ||
                entity instanceof Panda ||
                entity instanceof PolarBear ||
                entity instanceof Wolf ||
                entity instanceof ArmorStand /*||
                entity instanceof LeashHitch ||
                entity instanceof Painting ||
                entity instanceof ItemFrame ||
                entity instanceof GlowItemFrame*/


            ) {

                Chunk chunk = entity.getLocation().getChunk();

                if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;
                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

                if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.ATTACK_PASSIVE_MOB))){
                    event.setCancelled(true);
                };


            }
        }

        if(event.getDamager() instanceof Projectile) {

            if(((Projectile) event.getDamager()).getShooter() instanceof Player){
                Player player = (Player) ((Projectile) event.getDamager()).getShooter();
                Entity entity = event.getEntity();

                if(
                    entity instanceof Allay ||
                    entity instanceof Axolotl ||
                    entity instanceof Bat ||
                    entity instanceof Camel ||
                    entity instanceof Cat ||
                    entity instanceof Chicken ||
                    entity instanceof Cow ||
                    entity instanceof Donkey ||
                    entity instanceof Fox ||
                    entity instanceof Frog ||
                    entity instanceof Horse ||
                    entity instanceof Mule ||
                    entity instanceof Ocelot ||
                    entity instanceof Parrot ||
                    entity instanceof Pig ||
                    entity instanceof Rabbit ||
                    entity instanceof Sheep ||
                    entity instanceof SkeletonHorse ||
                    entity instanceof Sniffer ||
                    entity instanceof Snowman ||
                    entity instanceof Squid ||
                    entity instanceof Strider ||
                    entity instanceof Turtle ||
                    entity instanceof Villager ||
                    entity instanceof WanderingTrader ||
                    entity instanceof Fish ||
                    entity instanceof Bee ||
                    entity instanceof Dolphin ||
                    entity instanceof Goat ||
                    entity instanceof IronGolem ||
                    entity instanceof Llama ||
                    entity instanceof Panda ||
                    entity instanceof PolarBear ||
                    entity instanceof Wolf ||
                    entity instanceof ArmorStand /*||
                    entity instanceof LeashHitch ||
                    entity instanceof Painting ||
                    entity instanceof ItemFrame ||
                    entity instanceof GlowItemFrame*/

            ) {

                Chunk chunk = entity.getLocation().getChunk();

                if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;
                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

                    if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.ATTACK_PASSIVE_MOB))){
                        event.setCancelled(true);
                    };
                }
            }
        }

    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();

            if(event.getInventory() instanceof FurnaceInventory ||
                    event.getInventory() instanceof BlastFurnace ||
                    event.getInventory() instanceof Smoker) {


                Chunk chunk = event.getInventory().getLocation().getChunk();

                if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;
                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

                if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.USE_FURNACE))){
                    event.setCancelled(true);
                };




            }
        }
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof ItemFrame) {
            Player player = event.getPlayer();
            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

            Chunk chunk = itemFrame.getLocation().getChunk();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;
            TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

            if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.INTERACT_ITEM_FRAME))){
                event.setCancelled(true);
            };
        }

        if(event.getRightClicked() instanceof LeashHitch) {
            Player player = event.getPlayer();
            LeashHitch leashHitch = (LeashHitch) event.getRightClicked();

            Chunk chunk = leashHitch.getLocation().getChunk();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

            if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.LEAD))){
                event.setCancelled(true);
            };

        }

        if(event.getRightClicked() instanceof LivingEntity) {

            LivingEntity livingEntity = (LivingEntity) event.getRightClicked();

            if(livingEntity.isLeashed()) {

                Player player = event.getPlayer();
                Chunk chunk = livingEntity.getLocation().getChunk();

                if (!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

                if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.LEAD))){
                    event.setCancelled(true);
                };
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof ArmorStand) {
            Player player = event.getPlayer();
            ArmorStand armorStand = (ArmorStand) event.getRightClicked();

            Chunk chunk = armorStand.getLocation().getChunk();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
            if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.INTERACT_ARMOR_STAND))){
                event.setCancelled(true);
            };
        }
    }

    @EventHandler
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getLocation().getChunk();

        if (!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk,player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.LEAD))){
            event.setCancelled(true);
        }


    }

    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event){
        Entity remover = event.getRemover();
        if(remover instanceof Player player){
            Entity entity = event.getEntity();
            Chunk chunk = entity.getLocation().getChunk();
            if (!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;
            TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
            if(entity instanceof LeashHitch) {
                if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.LEAD))) {
                    event.setCancelled(true);
                }
            }
            else{
                if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.BREAK))) {
                    event.setCancelled(true);
                }
            }
        } else if (remover instanceof Projectile projectile) {
            if(projectile.getShooter() instanceof Player player){
                Entity entity = event.getEntity();
                Chunk chunk = entity.getLocation().getChunk();
                if (!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;
                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                if(entity instanceof LeashHitch) {
                    if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.LEAD))) {
                        event.setCancelled(true);
                    }
                }
                else{
                    if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.BREAK))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent event){
        Block block = event.getBlock();
        Chunk chunk = block.getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        Entity entity = event.getEntity();
        if(entity instanceof LeashHitch){
            if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.LEAD))){
                event.setCancelled(true);
            }
        } else {
            if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.PLACE))){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntityEvent(PlayerShearEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getLocation().getChunk();

        if (!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk,player,chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.SHEARS))){
            event.setCancelled(true);
        }
    }


    private boolean CanPlayerDoAction(Chunk chunk, Player player, TownChunkPermission permission){

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return true;

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        //Chunk is claimed yet player have no town
        if(PlayerDataStorage.get(player).getTownId() == null){
            playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
            return false;
        }


        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return true;
        //Same alliance
        if(permission == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return true;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getPermission(TownChunkPermissionType.BREAK) == TownChunkPermission.FOREIGN)
            return true;
        //war has been declared
        if(WarTaggedPlayer.isPlayerInWarWithTown(player,chunkTown))
            return true;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        return false;
    }



    private void playerCantPerformAction(Player player, String ChunkOwner){
        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
        player.sendMessage(getTANString() + Lang.CHUNK_BELONGS_TO.getTranslation(ChunkOwner));
    }


}
