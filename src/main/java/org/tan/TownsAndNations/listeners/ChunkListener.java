package org.tan.TownsAndNations.listeners;

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
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.enums.ChunkPermissionType;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.SudoPlayerStorage;

import static org.tan.TownsAndNations.enums.ChunkPermissionType.*;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Player player = event.getPlayer();
        Block breakedBlock = event.getBlock();
        Location loc = breakedBlock.getLocation();

        //Check if the block is a property sign
        if (breakedBlock.getType() == Material.OAK_SIGN) {
            Sign sign = (Sign) breakedBlock.getState();
            if (sign.hasMetadata("propertySign")) {
                event.setCancelled(true);
                return;
            }
        }

        if(!CanPlayerDoAction(loc, player,BREAK))
            event.setCancelled(true);
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if(!CanPlayerDoAction(loc, player,BREAK)){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if(!CanPlayerDoAction(loc, player,PLACE)){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null)
            return;

        BlockData blockData = block.getBlockData();
        Material materialType = block.getType();
        Material materialBlock = blockData.getMaterial();

        Location loc = block.getLocation();

        //Check if the block is a property sign
        if (block.getType() == Material.OAK_SIGN) {
            Sign sign = (Sign) block.getState();
            if (sign.hasMetadata("propertySign")) {
                event.setCancelled(true);
                return;
            }
        }

        if(Tag.BUTTONS.isTagged(materialType) ||
                materialBlock == Material.LEVER){

            if(!CanPlayerDoAction(loc, event.getPlayer(),USE_BUTTONS)){
                event.setCancelled(true);
            }
        }

        else if(materialBlock == Material.CHEST ||
                materialBlock == Material.TRAPPED_CHEST ||
                materialBlock == Material.BARREL ||
                materialBlock == Material.HOPPER ||
                materialBlock == Material.DISPENSER ||
                materialBlock == Material.DROPPER ||
                materialBlock == Material.BREWING_STAND){

            if(!CanPlayerDoAction(loc, player,CHEST)){
                event.setCancelled(true);
            }
        }
        else if(
                Tag.DOORS.isTagged(materialType) ||
                Tag.TRAPDOORS.isTagged(materialType) ||
                Tag.FENCE_GATES.isTagged(materialType)){
            if(!CanPlayerDoAction(loc, player,DOOR)){
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
            if(!CanPlayerDoAction(loc, player,DECORATIVE_BLOCK)){
                event.setCancelled(true);
            }
        }
        else if (
                materialBlock == Material.JUKEBOX ||
                materialBlock == Material.NOTE_BLOCK
        ) {
            if(!CanPlayerDoAction(loc, player,MUSIC_BLOCK)){
                event.setCancelled(true);
            }
        }
        else if (
                materialBlock == Material.REDSTONE_WIRE ||
                materialBlock == Material.REPEATER ||
                materialBlock == Material.COMPARATOR ||
                materialBlock == Material.DAYLIGHT_DETECTOR) {
            if(!CanPlayerDoAction(loc, player,USE_REDSTONE)){
                event.setCancelled(true);
            }
        }


        else if (event.getAction() == Action.PHYSICAL &&
                (event.getClickedBlock().getType() == Material.FARMLAND )){

            if(!CanPlayerDoAction(loc, player,BREAK)){
                event.setCancelled(true);
            }
        }


    }



    @EventHandler
    public void OnBlocPlaced(BlockPlaceEvent event){

        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();


        if(!CanPlayerDoAction(loc, player,PLACE)){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if(event.getDamager() instanceof Player player) {
            Entity entity = event.getEntity();
            Location loc = entity.getLocation();


            if( entity instanceof Allay ||
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
                entity instanceof ArmorStand
            ) {
                if(!CanPlayerDoAction(loc, player,ATTACK_PASSIVE_MOB)){
                    event.setCancelled(true);
                };
            }

            else if(entity instanceof ItemFrame) {
                if(!CanPlayerDoAction(loc, player,INTERACT_ITEM_FRAME)){
                    event.setCancelled(true);
                };
            }

            else if(entity instanceof EnderCrystal){
                if(!CanPlayerDoAction(loc, player, ChunkPermissionType.BREAK)){
                    event.setCancelled(true);
                };
            }
        }

        if(event.getDamager() instanceof Projectile) {

            if(((Projectile) event.getDamager()).getShooter() instanceof Player player){
                Entity entity = event.getEntity();
                Location loc = entity.getLocation();

                if(entity instanceof Allay ||
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
                    entity instanceof ArmorStand) {

                    if(!CanPlayerDoAction(loc, player,ATTACK_PASSIVE_MOB)){
                        event.setCancelled(true);
                    }
                }
                else if(entity instanceof ItemFrame) {
                    if(!CanPlayerDoAction(loc, player,INTERACT_ITEM_FRAME)){
                        event.setCancelled(true);
                    }
                }
                else if(entity instanceof EnderCrystal){
                    if(!CanPlayerDoAction(loc, player,BREAK)){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getPlayer() instanceof Player player){
            if(event.getInventory() instanceof FurnaceInventory ||
                    event.getInventory() instanceof BlastFurnace ||
                    event.getInventory() instanceof Smoker) {

                Location loc = event.getInventory().getLocation();

                if(!CanPlayerDoAction(loc, player,USE_FURNACE)){
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        Player player = event.getPlayer();

        if(event.getRightClicked() instanceof ItemFrame itemFrame) {
            Location loc = itemFrame.getLocation();

            if(!CanPlayerDoAction(loc, player,INTERACT_ITEM_FRAME)){
                event.setCancelled(true);
            }
        }

        else if(event.getRightClicked() instanceof LeashHitch leashHitch) {
            Location loc = leashHitch.getLocation();


            if(!CanPlayerDoAction(loc, player,LEAD)){
                event.setCancelled(true);
            }

        }

        else if(event.getRightClicked() instanceof LivingEntity livingEntity) {
            if(livingEntity.isLeashed()) {
                Location loc = livingEntity.getLocation();

                if(!CanPlayerDoAction(loc, player,LEAD)){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof ArmorStand armorStand) {
            Player player = event.getPlayer();
            Location loc = armorStand.getLocation();

            if(!CanPlayerDoAction(loc, player,INTERACT_ARMOR_STAND)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();

        if(!CanPlayerDoAction(loc,player,LEAD)){
            event.setCancelled(true);
        }


    }

    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event){
        Entity remover = event.getRemover();
        if(remover instanceof Player player){
            Entity entity = event.getEntity();
            Location loc = entity.getLocation();

            if(entity instanceof LeashHitch) {
                if (!CanPlayerDoAction(loc, player,LEAD)) {
                    event.setCancelled(true);
                }
            }
            else{
                if (!CanPlayerDoAction(loc, player,BREAK)) {
                    event.setCancelled(true);
                }
            }
        } else if (remover instanceof Projectile projectile) {
            if(projectile.getShooter() instanceof Player player){
                Entity entity = event.getEntity();
                Location loc = entity.getLocation();

                if(entity instanceof LeashHitch) {
                    if (!CanPlayerDoAction(loc, player,LEAD)) {
                        event.setCancelled(true);
                    }
                }
                else{
                    if (!CanPlayerDoAction(loc, player,BREAK)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHangingPlaceEvent(HangingPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        Entity entity = event.getEntity();
        if(entity instanceof LeashHitch){
            if(!CanPlayerDoAction(loc, player,LEAD)){
                event.setCancelled(true);
            }
        } else {
            if(!CanPlayerDoAction(loc, player,PLACE)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntityEvent(PlayerShearEntityEvent event){
        Player player = event.getPlayer();
        Location loc = event.getEntity().getLocation();

        if(!CanPlayerDoAction(loc,player,SHEARS)){
            event.setCancelled(true);
        }
    }


    private boolean CanPlayerDoAction(Location location, Player player, ChunkPermissionType permissionType){

        //Chunk not claimed
        if(!NewClaimedChunkStorage.isChunkClaimed(location.getChunk()))
            return true;

        //Player in admin mode
        if(SudoPlayerStorage.isSudoPlayer(player))
            return true;

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(location.getChunk());
        return claimedChunk.canPlayerDo(player, permissionType,location);

    }
}
