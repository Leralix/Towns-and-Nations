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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.FurnaceInventory;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.enums.ChunkPermissionType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.SudoPlayerStorage;

import static org.leralix.tan.enums.ChunkPermissionType.*;

public class ChunkListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        Player player = event.getPlayer();
        Block breakedBlock = event.getBlock();
        Location loc = breakedBlock.getLocation();

        //Check if the block is a property sign
        if (breakedBlock.hasMetadata("propertySign")) {
            event.setCancelled(true);
            return;
        }

        if(!canPlayerDoAction(loc, player, BREAK_BLOCK))
            event.setCancelled(true);
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if(!canPlayerDoAction(loc, player, BREAK_BLOCK)){
            event.setCancelled(true);
        }

    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();

        if(!canPlayerDoAction(loc, player, PLACE_BLOCK)){
            event.setCancelled(true);
        }

    }
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
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

            if(!canPlayerDoAction(loc, event.getPlayer(), INTERACT_BUTTON)){
                event.setCancelled(true);
            }
        }

        else if(materialBlock == Material.CHEST ||
                materialBlock == Material.TRAPPED_CHEST ||
                materialBlock == Material.BARREL ||
                materialBlock == Material.HOPPER ||
                materialBlock == Material.DISPENSER ||
                materialBlock == Material.DROPPER ||
                materialBlock == Material.BREWING_STAND ||
                materialBlock == Material.SHULKER_BOX){

            if(!canPlayerDoAction(loc, player, INTERACT_CHEST)){
                event.setCancelled(true);
            }
        }
        else if(
                Tag.DOORS.isTagged(materialType) ||
                Tag.TRAPDOORS.isTagged(materialType) ||
                Tag.FENCE_GATES.isTagged(materialType)){
            if(!canPlayerDoAction(loc, player, INTERACT_DOOR)){
                event.setCancelled(true);
            }
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
            if(!canPlayerDoAction(loc, player, INTERACT_DECORATIVE_BLOCK)){
                event.setCancelled(true);
            }
        }
        else if (
                materialBlock == Material.JUKEBOX ||
                materialBlock == Material.NOTE_BLOCK
        ) {
            if(!canPlayerDoAction(loc, player, INTERACT_MUSIC_BLOCK)){
                event.setCancelled(true);
            }
        }
        else if (
                materialBlock == Material.REDSTONE_WIRE ||
                materialBlock == Material.REPEATER ||
                materialBlock == Material.COMPARATOR ||
                materialBlock == Material.DAYLIGHT_DETECTOR) {
            if(!canPlayerDoAction(loc, player, INTERACT_REDSTONE)){
                event.setCancelled(true);
            }
        }

        else if(event.getItem() != null && event.getItem().getType() == Material.BONE_MEAL){
            if(!canPlayerDoAction(loc, player, USE_BONE_MEAL)){
                event.setCancelled(true);
            }
        }

        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SWEET_BERRY_BUSH){
            if(!canPlayerDoAction(loc, player, INTERACT_BERRIES))
                event.setCancelled(true);
        }
        else if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType() == Material.OAK_BOAT) {
            if(!canPlayerDoAction(loc, player, INTERACT_BOAT))
                event.setCancelled(true);
        }
        else if((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && (player.getItemInHand().getType() == Material.MINECART)){
            if(!canPlayerDoAction(loc, player, INTERACT_MINECART))
                event.setCancelled(true);
        }

        else if (event.getAction() == Action.PHYSICAL &&
                (event.getClickedBlock().getType() == Material.FARMLAND )){

            if(!canPlayerDoAction(loc, player, BREAK_BLOCK)){
                event.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void onBlocPlaced(BlockPlaceEvent event){

        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();


        if(!canPlayerDoAction(loc, player, PLACE_BLOCK)){
            event.setCancelled(true);
        }

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
                if(!canPlayerDoAction(loc, player,ATTACK_PASSIVE_MOB)){
                    event.setCancelled(true);
                };
            }

            else if(entity instanceof ItemFrame) {
                if(!canPlayerDoAction(loc, player,INTERACT_ITEM_FRAME)){
                    event.setCancelled(true);
                };
            }

            else if(entity instanceof EnderCrystal){
                if(!canPlayerDoAction(loc, player, ChunkPermissionType.BREAK_BLOCK)){
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

                    if(!canPlayerDoAction(loc, player,ATTACK_PASSIVE_MOB)){
                        event.setCancelled(true);
                    }
                }
                else if(entity instanceof ItemFrame) {
                    if(!canPlayerDoAction(loc, player,INTERACT_ITEM_FRAME)){
                        event.setCancelled(true);
                    }
                }
                else if(entity instanceof EnderCrystal){
                    if(!canPlayerDoAction(loc, player, BREAK_BLOCK)){
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
                if(loc == null)
                    return;
                if(!canPlayerDoAction(loc, player, INTERACT_FURNACE)){
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

            if(!canPlayerDoAction(loc, player,INTERACT_ITEM_FRAME)){
                event.setCancelled(true);
            }
        }

        else if(event.getRightClicked() instanceof LeashHitch leashHitch) {
            Location loc = leashHitch.getLocation();


            if(!canPlayerDoAction(loc, player, USE_LEAD)){
                event.setCancelled(true);
            }

        }

        else if(event.getRightClicked() instanceof LivingEntity livingEntity) {
            if(livingEntity.isLeashed()) {
                Location loc = livingEntity.getLocation();

                if(!canPlayerDoAction(loc, player, USE_LEAD)){
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

            if(!canPlayerDoAction(loc, player,INTERACT_ARMOR_STAND)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();

        if(!canPlayerDoAction(loc,player, USE_LEAD)){
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
                if (!canPlayerDoAction(loc, player, USE_LEAD)) {
                    event.setCancelled(true);
                }
            }
            else{
                if (!canPlayerDoAction(loc, player, BREAK_BLOCK)) {
                    event.setCancelled(true);
                }
            }
        } else if (remover instanceof Projectile projectile) {
            if(projectile.getShooter() instanceof Player player){
                Entity entity = event.getEntity();
                Location loc = entity.getLocation();

                if(entity instanceof LeashHitch) {
                    if (!canPlayerDoAction(loc, player, USE_LEAD)) {
                        event.setCancelled(true);
                    }
                }
                else{
                    if (!canPlayerDoAction(loc, player, BREAK_BLOCK)) {
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
            if(!canPlayerDoAction(loc, player, USE_LEAD)){
                event.setCancelled(true);
            }
        } else {
            if(!canPlayerDoAction(loc, player, PLACE_BLOCK)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerShearEntityEvent(PlayerShearEntityEvent event){
        Player player = event.getPlayer();
        Location loc = event.getEntity().getLocation();

        if(!canPlayerDoAction(loc,player, USE_SHEARS)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event){
        event.blockList().removeIf(block -> !NewClaimedChunkStorage.get(block.getChunk()).canExplosionGrief());
    }

    @EventHandler
    public void onBurning(BlockBurnEvent event){
        Chunk chunk = event.getBlock().getChunk();

        if(NewClaimedChunkStorage.get(chunk).canFireGrief()){
            event.setCancelled(true);
        }
    }


    private boolean canPlayerDoAction(Location location, Player player, ChunkPermissionType permissionType){

        //Player in admin mode
        if(SudoPlayerStorage.isSudoPlayer(player))
            return true;

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(location.getChunk());
        PlayerData playerData = PlayerDataStorage.get(player);

        if(playerData.isAtWarWith(claimedChunk.getOwner()))
            return true;


        return claimedChunk.canPlayerDo(player, permissionType,location);

    }


}
