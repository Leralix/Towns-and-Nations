package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
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

import static org.tan.TownsAndNations.enums.ChunkPermissionType.*;

public class ChunkListener implements Listener {

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        if(!CanPlayerDoAction(chunk, event.getPlayer(),BREAK)){
            event.setCancelled(true);
        };
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;


        if(!CanPlayerDoAction(chunk, event.getPlayer(),BREAK)){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        if(!CanPlayerDoAction(chunk, event.getPlayer(),PLACE)){
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

            if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(
                Tag.BUTTONS.isTagged(materialType) ||
                materialBlock == Material.LEVER
            ){
                if(!CanPlayerDoAction(chunk, event.getPlayer(),USE_BUTTONS)){
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
                if(!CanPlayerDoAction(chunk, event.getPlayer(),CHEST)){
                    event.setCancelled(true);
                }
            }
            else if(
                    Tag.DOORS.isTagged(materialType) ||
                    Tag.TRAPDOORS.isTagged(materialType) ||
                    Tag.FENCE_GATES.isTagged(materialType)){
                if(!CanPlayerDoAction(chunk, event.getPlayer(),DOOR)){
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
                if(!CanPlayerDoAction(chunk, event.getPlayer(),DECORATIVE_BLOCK)){
                    event.setCancelled(true);
                }
            }
            else if (
                    materialBlock == Material.JUKEBOX ||
                    materialBlock == Material.NOTE_BLOCK
            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),MUSIC_BLOCK)){
                    event.setCancelled(true);
                }
            }
            else if (
                    materialBlock == Material.REDSTONE_WIRE ||
                    materialBlock == Material.REPEATER ||
                    materialBlock == Material.COMPARATOR ||
                    materialBlock == Material.DAYLIGHT_DETECTOR) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),USE_REDSTONE)){
                    event.setCancelled(true);
                }
            }


            else if (event.getAction() == Action.PHYSICAL &&
                    (event.getClickedBlock().getType() == Material.FARMLAND )){
                    //event.getClickedBlock().getType() == Material.LEGACY_SOIL)) { // LEGACY

                if(!CanPlayerDoAction(chunk, event.getPlayer(),BREAK)){
                    event.setCancelled(true);
                };
            }
        }


    }



    @EventHandler
    public void OnBlocPlaced(BlockPlaceEvent event){

        Block block = event.getBlock();
        Chunk chunk = block.getLocation().getChunk();

        if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        if(!CanPlayerDoAction(chunk, event.getPlayer(),PLACE)){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player player) {
            Entity entity = event.getEntity();


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
                entity instanceof ArmorStand /*||
                entity instanceof LeashHitch ||
                entity instanceof Painting ||
                entity instanceof ItemFrame ||
                entity instanceof GlowItemFrame*/
            ) {

                Chunk chunk = entity.getLocation().getChunk();

                if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                if(!CanPlayerDoAction(chunk, player,ATTACK_PASSIVE_MOB)){
                    event.setCancelled(true);
                };
            }

            if(entity instanceof ItemFrame) {
                Chunk chunk = entity.getLocation().getChunk();

                if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                if(!CanPlayerDoAction(chunk, player,INTERACT_ITEM_FRAME)){
                    event.setCancelled(true);
                };
            }

            if(entity instanceof EnderCrystal){
                Chunk chunk = entity.getLocation().getChunk();

                if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                if(!CanPlayerDoAction(chunk, player, ChunkPermissionType.BREAK)){
                    event.setCancelled(true);
                };
            }


        }

        if(event.getDamager() instanceof Projectile) {

            if(((Projectile) event.getDamager()).getShooter() instanceof Player player){
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
                    entity instanceof GlowItemFrame*/) {

                    Chunk chunk = entity.getLocation().getChunk();

                    if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                        return;

                    if(!CanPlayerDoAction(chunk, player,ATTACK_PASSIVE_MOB)){
                        event.setCancelled(true);
                    }
                }
                if(entity instanceof ItemFrame) {
                    Chunk chunk = entity.getLocation().getChunk();

                    if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                        return;

                    if(!CanPlayerDoAction(chunk, player,INTERACT_ITEM_FRAME)){
                        event.setCancelled(true);
                    };
                }
                if(entity instanceof EnderCrystal){
                    Chunk chunk = entity.getLocation().getChunk();

                    if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                        return;

                    if(!CanPlayerDoAction(chunk, player,BREAK)){
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

                if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                if(!CanPlayerDoAction(chunk, player,USE_FURNACE)){
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

            if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(!CanPlayerDoAction(chunk, player,INTERACT_ITEM_FRAME)){
                event.setCancelled(true);
            }
        }

        if(event.getRightClicked() instanceof LeashHitch) {
            Player player = event.getPlayer();
            LeashHitch leashHitch = (LeashHitch) event.getRightClicked();

            Chunk chunk = leashHitch.getLocation().getChunk();

            if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(!CanPlayerDoAction(chunk, player,LEAD)){
                event.setCancelled(true);
            };

        }

        if(event.getRightClicked() instanceof LivingEntity) {

            LivingEntity livingEntity = (LivingEntity) event.getRightClicked();

            if(livingEntity.isLeashed()) {

                Player player = event.getPlayer();
                Chunk chunk = livingEntity.getLocation().getChunk();

                if (!NewClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                if(!CanPlayerDoAction(chunk, player,LEAD)){
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

            if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(!CanPlayerDoAction(chunk, player,INTERACT_ARMOR_STAND)){
                event.setCancelled(true);
            };
        }
    }

    @EventHandler
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getLocation().getChunk();

        if (!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        if(!CanPlayerDoAction(chunk,player,LEAD)){
            event.setCancelled(true);
        }


    }

    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event){
        Entity remover = event.getRemover();
        if(remover instanceof Player player){
            Entity entity = event.getEntity();
            Chunk chunk = entity.getLocation().getChunk();
            if (!NewClaimedChunkStorage.isChunkClaimed(chunk))
                return;
            if(entity instanceof LeashHitch) {
                if (!CanPlayerDoAction(chunk, player,LEAD)) {
                    event.setCancelled(true);
                }
            }
            else{
                if (!CanPlayerDoAction(chunk, player,BREAK)) {
                    event.setCancelled(true);
                }
            }
        } else if (remover instanceof Projectile projectile) {
            if(projectile.getShooter() instanceof Player player){
                Entity entity = event.getEntity();
                Chunk chunk = entity.getLocation().getChunk();
                if (!NewClaimedChunkStorage.isChunkClaimed(chunk))
                    return;
                if(entity instanceof LeashHitch) {
                    if (!CanPlayerDoAction(chunk, player,LEAD)) {
                        event.setCancelled(true);
                    }
                }
                else{
                    if (!CanPlayerDoAction(chunk, player,BREAK)) {
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

        if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        Entity entity = event.getEntity();
        if(entity instanceof LeashHitch){
            if(!CanPlayerDoAction(chunk, event.getPlayer(),LEAD)){
                event.setCancelled(true);
            }
        } else {
            if(!CanPlayerDoAction(chunk, event.getPlayer(),PLACE)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntityEvent(PlayerShearEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        Chunk chunk = entity.getLocation().getChunk();

        if (!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        if(!CanPlayerDoAction(chunk,player,SHEARS)){
            event.setCancelled(true);
        }
    }


    private boolean CanPlayerDoAction(Chunk chunk, Player player, ChunkPermissionType permissionType){

        //Chunk not claimed
        if(!NewClaimedChunkStorage.isChunkClaimed(chunk))
            return true;

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(chunk);
        return claimedChunk.canPlayerDo(player, permissionType);

    }
}
