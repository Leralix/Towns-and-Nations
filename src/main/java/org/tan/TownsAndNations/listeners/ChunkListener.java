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

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getBreakAuth())){
            event.setCancelled(true);
        };
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getBreakAuth())){
            event.setCancelled(true);
        };

    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;
        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPlaceAuth())){
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
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getUseButtonsAuth())){
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
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getChestAuth())){
                    event.setCancelled(true);
                }
            }
            else if(
                    /*
                    materialBlock == Material.OAK_DOOR ||
                    materialBlock == Material.SPRUCE_DOOR ||
                    materialBlock == Material.ACACIA_DOOR ||
                    materialBlock == Material.DARK_OAK_DOOR ||
                    materialBlock == Material.BAMBOO_DOOR ||
                    materialBlock == Material.BIRCH_DOOR ||
                    materialBlock == Material.CRIMSON_DOOR ||
                    materialBlock == Material.JUNGLE_DOOR ||
                    materialBlock == Material.WARPED_DOOR ||
                    materialBlock == Material.MANGROVE_DOOR ||
                    materialBlock == Material.CHERRY_DOOR ||
                    materialBlock == Material.IRON_DOOR ||
                     */
                    Tag.DOORS.isTagged(materialType) ||

                    /*
                    materialBlock == Material.OAK_TRAPDOOR ||
                    materialBlock == Material.SPRUCE_TRAPDOOR ||
                    materialBlock == Material.ACACIA_TRAPDOOR ||
                    materialBlock == Material.DARK_OAK_TRAPDOOR ||
                    materialBlock == Material.BAMBOO_TRAPDOOR ||
                    materialBlock == Material.BIRCH_TRAPDOOR ||
                    materialBlock == Material.CRIMSON_TRAPDOOR ||
                    materialBlock == Material.JUNGLE_TRAPDOOR ||
                    materialBlock == Material.WARPED_TRAPDOOR ||
                    materialBlock == Material.MANGROVE_TRAPDOOR ||
                    materialBlock == Material.CHERRY_TRAPDOOR ||
                    materialBlock == Material.IRON_TRAPDOOR ||
                    */
                    Tag.TRAPDOORS.isTagged(materialType) ||

                    /*
                    materialBlock == Material.OAK_FENCE_GATE ||
                    materialBlock == Material.SPRUCE_FENCE_GATE ||
                    materialBlock == Material.ACACIA_FENCE_GATE ||
                    materialBlock == Material.DARK_OAK_FENCE_GATE ||
                    materialBlock == Material.BAMBOO_FENCE_GATE ||
                    materialBlock == Material.BIRCH_FENCE_GATE ||
                    materialBlock == Material.CRIMSON_FENCE_GATE ||
                    materialBlock == Material.JUNGLE_FENCE_GATE ||
                    materialBlock == Material.WARPED_FENCE_GATE ||
                    materialBlock == Material.MANGROVE_FENCE_GATE ||
                    materialBlock == Material.CHERRY_FENCE_GATE
                     */
                    Tag.FENCE_GATES.isTagged(materialType)

            ){
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getDoorAuth())){
                    event.setCancelled(true);
                };
            }
            else if (
                    /*
                    materialBlock == Material.CANDLE ||
                    materialBlock == Material.WHITE_CANDLE ||
                    materialBlock == Material.LIGHT_GRAY_CANDLE ||
                    materialBlock == Material.GRAY_CANDLE ||
                    materialBlock == Material.BLACK_CANDLE ||
                    materialBlock == Material.BROWN_CANDLE ||
                    materialBlock == Material.RED_CANDLE ||
                    materialBlock == Material.ORANGE_CANDLE ||
                    materialBlock == Material.YELLOW_CANDLE ||
                    materialBlock == Material.LIME_CANDLE ||
                    materialBlock == Material.GREEN_CANDLE ||
                    materialBlock == Material.CYAN_CANDLE ||
                    materialBlock == Material.LIGHT_BLUE_CANDLE ||
                    materialBlock == Material.BLUE_CANDLE ||
                    materialBlock == Material.PURPLE_CANDLE ||
                    materialBlock == Material.MAGENTA_CANDLE ||
                    materialBlock == Material.PINK_CANDLE ||
                     */
                    Tag.CANDLES.isTagged(materialType) ||
                    Tag.CANDLE_CAKES.isTagged(materialType) ||

                    Tag.FLOWER_POTS.isTagged(materialType) ||

                    //materialBlock == Material.FLOWER_POT ||

                    /*
                    materialBlock == Material.CAULDRON ||
                    materialBlock == Material.LAVA_CAULDRON ||
                    materialBlock == Material.WATER_CAULDRON ||
                    materialBlock == Material.POWDER_SNOW_CAULDRON ||
                     */

                    Tag.CAULDRONS.isTagged(materialType) ||

                    materialBlock == Material.COMPOSTER ||

                    Tag.ALL_SIGNS.isTagged(materialType) ||
                    /*
                    materialBlock == Material.OAK_SIGN ||
                    materialBlock == Material.OAK_HANGING_SIGN ||
                    materialBlock == Material.SPRUCE_SIGN ||
                    materialBlock == Material.SPRUCE_HANGING_SIGN ||
                    materialBlock == Material.BIRCH_SIGN ||
                    materialBlock == Material.BIRCH_HANGING_SIGN ||
                    materialBlock == Material.JUNGLE_SIGN ||
                    materialBlock == Material.JUNGLE_HANGING_SIGN ||
                    materialBlock == Material.ACACIA_SIGN ||
                    materialBlock == Material.ACACIA_HANGING_SIGN ||
                    materialBlock == Material.DARK_OAK_SIGN ||
                    materialBlock == Material.DARK_OAK_HANGING_SIGN ||
                    materialBlock == Material.MANGROVE_SIGN ||
                    materialBlock == Material.MANGROVE_HANGING_SIGN ||
                    materialBlock == Material.CHERRY_SIGN ||
                    materialBlock == Material.CHERRY_HANGING_SIGN ||
                    materialBlock == Material.BAMBOO_SIGN ||
                    materialBlock == Material.BAMBOO_HANGING_SIGN ||
                    materialBlock == Material.CRIMSON_SIGN ||
                    materialBlock == Material.CRIMSON_HANGING_SIGN ||
                    materialBlock == Material.WARPED_SIGN ||
                    materialBlock == Material.WARPED_HANGING_SIGN ||
                     */

                    materialBlock == Material.CHISELED_BOOKSHELF ||

                    Tag.CAMPFIRES.isTagged(materialType) ||
                    /*
                    materialBlock == Material.CAMPFIRE ||
                    materialBlock == Material.SOUL_CAMPFIRE ||
                    */

                    materialBlock == Material.BEACON

            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getDecorativeBlockAuth())){
                    event.setCancelled(true);
                }
            }
            else if (
                    materialBlock == Material.JUKEBOX ||
                    materialBlock == Material.NOTE_BLOCK
            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getMusicBlockAuth())){
                    event.setCancelled(true);
                }
            }
            else if (
                    materialBlock == Material.REDSTONE_WIRE ||
                    materialBlock == Material.REPEATER ||
                    materialBlock == Material.COMPARATOR ||
                    materialBlock == Material.DAYLIGHT_DETECTOR
            ) {
                if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getUseRedstoneAuth())){
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

        if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPlaceAuth())){
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

                if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getAttackPassiveMobAuth())){
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

                    if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getAttackPassiveMobAuth())){
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

                if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getUseFurnaceAuth())){
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

            if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getInteractItemFrameAuth())){
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

            if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getLeadAuth())){
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

                if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getLeadAuth())){
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
            if(!CanPlayerDoAction(chunk, player,chunkTown.getChunkSettings().getInteractArmorStandAuth())){
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

        if(!CanPlayerDoAction(chunk,player,chunkTown.getChunkSettings().getLeadAuth())){
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
                if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getLeadAuth())) {
                    event.setCancelled(true);
                }
            }
            else{
                if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getBreakAuth())) {
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
                    if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getLeadAuth())) {
                        event.setCancelled(true);
                    }
                }
                else{
                    if (!CanPlayerDoAction(chunk, player, chunkTown.getChunkSettings().getBreakAuth())) {
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
            if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getLeadAuth())){
                event.setCancelled(true);
            }
        } else {
            if(!CanPlayerDoAction(chunk, event.getPlayer(),chunkTown.getChunkSettings().getPlaceAuth())){
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

        if(!CanPlayerDoAction(chunk,player,chunkTown.getChunkSettings().getShearsAuth())){
            event.setCancelled(true);
        }
    }


    private boolean CanPlayerDoAction(Chunk chunk, Player player, TownChunkPermission permission){

        //Chunk is not claimed
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
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
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
