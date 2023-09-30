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
        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        if(PlayerDataStorage.get(player).getTownId() == null){
            playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
            event.setCancelled(true);
            return;
        }


        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
            return;
        //war has been declared
        if(WarTaggedPlayer.isPlayerInWarWithTown(player,chunkTown))
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }
    @EventHandler
    public void onBucketFillEvent(PlayerBucketFillEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        if(PlayerDataStorage.get(player).getTownId() == null){
            event.setCancelled(true);
            return;
        }

        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
            return;
        //war has been declared
        if(WarTaggedPlayer.isPlayerInWarWithTown(player,chunkTown))
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);

    }
    @EventHandler
    public void onBucketEmptyEvent(PlayerBucketEmptyEvent event){

        Chunk chunk = event.getBlock().getLocation().getChunk();

        if(!ClaimedChunkStorage.isChunkClaimed(chunk))
            return;

        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        if(PlayerDataStorage.get(player).getTownId() == null){
            event.setCancelled(true);
            return;
        }

        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //permission is on foreign
        if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
            return;
        //war has been declared
        if(WarTaggedPlayer.isPlayerInWarWithTown(player,chunkTown))
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);

    }
    @EventHandler
    public void OnContainersOpen(PlayerInteractEvent event){

        Block block = event.getClickedBlock();
        if (block != null){

            BlockData blockData = block.getBlockData();

            Chunk chunk = block.getLocation().getChunk();
            Player player = event.getPlayer();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            if(PlayerDataStorage.get(player).getTownId() == null){
                event.setCancelled(true);
                return;
            }
            Material materialType = block.getType();
            Material materialBlock = blockData.getMaterial();

            if(
                    materialBlock == Material.CHEST ||
                    materialBlock == Material.TRAPPED_CHEST ||
                    materialBlock == Material.BARREL ||
                    materialBlock == Material.HOPPER ||
                    materialBlock == Material.DISPENSER ||
                    materialBlock == Material.DROPPER ||
                    materialBlock == Material.BREWING_STAND

            ){


                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getBreakAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);

            }
            else if(
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

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getDoorAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getDoorAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);
            }
            else if (
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

                    materialBlock == Material.FLOWER_POT ||

                    materialBlock == Material.CAULDRON ||
                    materialBlock == Material.LAVA_CAULDRON ||
                    materialBlock == Material.WATER_CAULDRON ||
                    materialBlock == Material.POWDER_SNOW_CAULDRON ||

                    materialBlock == Material.COMPOSTER ||

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

                    materialBlock == Material.CHISELED_BOOKSHELF ||

                    materialBlock == Material.CAMPFIRE ||
                    materialBlock == Material.SOUL_CAMPFIRE ||

                    materialBlock == Material.BEACON

            ) {

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getDecorativeBlockAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getDecorativeBlockAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);

            }
            else if (
                    materialBlock == Material.JUKEBOX ||
                    materialBlock == Material.NOTE_BLOCK
            ) {

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getMusicBlockAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //permission is on foreign
                if(chunkTown.getChunkSettings().getMusicBlockAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);

            }
        }
    }
    @EventHandler
    public void OnBlocPlaced(BlockPlaceEvent event){

        Block block = event.getBlock();
        Chunk chunk = block.getLocation().getChunk();
        if(!ClaimedChunkStorage.isChunkClaimed(chunk)){
            return;
        }
        if(PlayerDataStorage.get(event.getPlayer()).getTownId() == null){
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
        TownData playerTown = TownDataStorage.get(player);

        //Same town
        if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if(chunkTown.getChunkSettings().getPlaceAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
            return;
        //Permission is on foreign
        if(chunkTown.getChunkSettings().getPlaceAuth() == TownChunkPermission.FOREIGN)
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
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
                    entity instanceof ArmorStand ||
                    entity instanceof LeashHitch

            ) {

                Chunk chunk = entity.getLocation().getChunk();

                if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getAttackPassiveMobAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //Permission is on foreign
                if(chunkTown.getChunkSettings().getAttackPassiveMobAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);


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
                        entity instanceof ArmorStand||
                        entity instanceof LeashHitch

                ) {

                    Chunk chunk = entity.getLocation().getChunk();

                    if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                        return;

                    TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                    TownData playerTown = TownDataStorage.get(player);

                    //Same town
                    if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                        return;
                    //Same alliance
                    if(chunkTown.getChunkSettings().getAttackPassiveMobAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                        return;
                    //Permission is on foreign
                    if(chunkTown.getChunkSettings().getAttackPassiveMobAuth() == TownChunkPermission.FOREIGN)
                        return;

                    playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                    event.setCancelled(true);


                }
            }

        }
    }
    //Button
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Material material = event.getClickedBlock().getType();
            Player player = event.getPlayer();

            if(isButton(material)) {

                Chunk chunk = event.getClickedBlock().getLocation().getChunk();

                if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getUseButtonsAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //Permission is on foreign
                if(chunkTown.getChunkSettings().getUseButtonsAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);
            }

            if(material == Material.LEVER) {

                Chunk blocChunk = event.getClickedBlock().getLocation().getChunk();

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(blocChunk));
                TownData playerTown = TownDataStorage.get(player);

                if(ClaimedChunkStorage.getChunkOwnerID(blocChunk).equals(playerTown.getID()))
                    return;
                if(chunkTown.getChunkSettings().getUseLeverAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                if(chunkTown.getChunkSettings().getUseLeverAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(blocChunk));
                event.setCancelled(true);
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
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if(chunkTown.getChunkSettings().getUseFurnaceAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                    return;
                //Permission is on foreign
                if(chunkTown.getChunkSettings().getUseFurnaceAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);




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
            TownData playerTown = TownDataStorage.get(player);

            //Same town
            if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                return;
            //Same alliance
            if(chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                return;
            //Permission is on foreign
            if(chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.FOREIGN)
                return;

            playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
            event.setCancelled(true);
        }

        if(event.getRightClicked() instanceof LeashHitch) {
            Player player = event.getPlayer();
            LeashHitch leashHitch = (LeashHitch) event.getRightClicked();

            Chunk chunk = leashHitch.getLocation().getChunk();

            if(!ClaimedChunkStorage.isChunkClaimed(chunk))
                return;

            TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
            TownData playerTown = TownDataStorage.get(player);

            //Same town
            if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                return;
            //Same alliance
            if(chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                return;
            //Permission is on foreign
            if(chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.FOREIGN)
                return;

            playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
            event.setCancelled(true);
        }

        if(event.getRightClicked() instanceof LivingEntity) {

            LivingEntity livingEntity = (LivingEntity) event.getRightClicked();

            if(livingEntity.isLeashed()) {

                Player player = event.getPlayer();
                Chunk chunk = livingEntity.getLocation().getChunk();

                if (!ClaimedChunkStorage.isChunkClaimed(chunk))
                    return;

                TownData chunkTown = TownDataStorage.get(ClaimedChunkStorage.getChunkOwnerID(chunk));
                TownData playerTown = TownDataStorage.get(player);

                //Same town
                if (ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                    return;
                //Same alliance
                if (chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE, playerTown.getID()))
                    return;
                //Permission is on foreign
                if (chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.FOREIGN)
                    return;

                playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
                event.setCancelled(true);
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
            TownData playerTown = TownDataStorage.get(player);

            //Same town
            if(ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
                return;
            //Same alliance
            if(chunkTown.getChunkSettings().getInteractArmorStandAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE,playerTown.getID()))
                return;
            //Permission is on foreign
            if(chunkTown.getChunkSettings().getInteractArmorStandAuth() == TownChunkPermission.FOREIGN)
                return;

            playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
            event.setCancelled(true);
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
        TownData playerTown = TownDataStorage.get(player);

        //Same town
        if (ClaimedChunkStorage.getChunkOwnerID(chunk).equals(playerTown.getID()))
            return;
        //Same alliance
        if (chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.ALLIANCE && chunkTown.getTownRelation(TownRelation.ALLIANCE, playerTown.getID()))
            return;
        //Permission is on foreign
        if (chunkTown.getChunkSettings().getInteractItemFrameAuth() == TownChunkPermission.FOREIGN)
            return;

        playerCantPerformAction(player, ClaimedChunkStorage.getChunkOwnerName(chunk));
        event.setCancelled(true);
    }

    private boolean isButton(Material material) {
        return material == Material.STONE_BUTTON || material == Material.OAK_BUTTON ||
                material == Material.SPRUCE_BUTTON || material == Material.BIRCH_BUTTON ||
                material == Material.JUNGLE_BUTTON || material == Material.ACACIA_BUTTON ||
                material == Material.DARK_OAK_BUTTON || material == Material.CRIMSON_BUTTON ||
                material == Material.WARPED_BUTTON || material == Material.POLISHED_BLACKSTONE_BUTTON;
    }

    private void playerCantPerformAction(Player player, String ChunkOwner){
        player.sendMessage(getTANString() + Lang.PLAYER_NO_PERMISSION.getTranslation());
        player.sendMessage(getTANString() + Lang.CHUNK_BELONGS_TO.getTranslation(ChunkOwner));
    }


}
