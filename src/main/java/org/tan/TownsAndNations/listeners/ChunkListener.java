package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.Material;
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
    public void onBucketEvent(PlayerBucketEvent event){

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
            if(
                    blockData.getMaterial() == Material.CHEST ||
                    blockData.getMaterial() == Material.TRAPPED_CHEST ||
                    blockData.getMaterial() == Material.BARREL ||
                    blockData.getMaterial() == Material.HOPPER ||
                    blockData.getMaterial() == Material.DISPENSER ||
                    blockData.getMaterial() == Material.DROPPER ||
                    blockData.getMaterial() == Material.BREWING_STAND

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
                    blockData.getMaterial() == Material.OAK_DOOR ||
                    blockData.getMaterial() == Material.SPRUCE_DOOR ||
                    blockData.getMaterial() == Material.ACACIA_DOOR ||
                    blockData.getMaterial() == Material.DARK_OAK_DOOR ||
                    blockData.getMaterial() == Material.BAMBOO_DOOR ||
                    blockData.getMaterial() == Material.BIRCH_DOOR ||
                    blockData.getMaterial() == Material.CRIMSON_DOOR ||
                    blockData.getMaterial() == Material.JUNGLE_DOOR ||
                    blockData.getMaterial() == Material.WARPED_DOOR ||
                    blockData.getMaterial() == Material.MANGROVE_DOOR ||
                    blockData.getMaterial() == Material.CHERRY_DOOR ||
                    blockData.getMaterial() == Material.IRON_DOOR ||


                    blockData.getMaterial() == Material.OAK_TRAPDOOR ||
                    blockData.getMaterial() == Material.SPRUCE_TRAPDOOR ||
                    blockData.getMaterial() == Material.ACACIA_TRAPDOOR ||
                    blockData.getMaterial() == Material.DARK_OAK_TRAPDOOR ||
                    blockData.getMaterial() == Material.BAMBOO_TRAPDOOR ||
                    blockData.getMaterial() == Material.BIRCH_TRAPDOOR ||
                    blockData.getMaterial() == Material.CRIMSON_TRAPDOOR ||
                    blockData.getMaterial() == Material.JUNGLE_TRAPDOOR ||
                    blockData.getMaterial() == Material.WARPED_TRAPDOOR ||
                    blockData.getMaterial() == Material.MANGROVE_TRAPDOOR ||
                    blockData.getMaterial() == Material.CHERRY_TRAPDOOR ||
                    blockData.getMaterial() == Material.IRON_TRAPDOOR ||

                    blockData.getMaterial() == Material.OAK_FENCE_GATE ||
                    blockData.getMaterial() == Material.SPRUCE_FENCE_GATE ||
                    blockData.getMaterial() == Material.ACACIA_FENCE_GATE ||
                    blockData.getMaterial() == Material.DARK_OAK_FENCE_GATE ||
                    blockData.getMaterial() == Material.BAMBOO_FENCE_GATE ||
                    blockData.getMaterial() == Material.BIRCH_FENCE_GATE ||
                    blockData.getMaterial() == Material.CRIMSON_FENCE_GATE ||
                    blockData.getMaterial() == Material.JUNGLE_FENCE_GATE ||
                    blockData.getMaterial() == Material.WARPED_FENCE_GATE ||
                    blockData.getMaterial() == Material.MANGROVE_FENCE_GATE ||
                    blockData.getMaterial() == Material.CHERRY_FENCE_GATE

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
                    blockData.getMaterial() == Material.CANDLE ||
                    blockData.getMaterial() == Material.WHITE_CANDLE ||
                    blockData.getMaterial() == Material.LIGHT_GRAY_CANDLE ||
                    blockData.getMaterial() == Material.GRAY_CANDLE ||
                    blockData.getMaterial() == Material.BLACK_CANDLE ||
                    blockData.getMaterial() == Material.BROWN_CANDLE ||
                    blockData.getMaterial() == Material.RED_CANDLE ||
                    blockData.getMaterial() == Material.ORANGE_CANDLE ||
                    blockData.getMaterial() == Material.YELLOW_CANDLE ||
                    blockData.getMaterial() == Material.LIME_CANDLE ||
                    blockData.getMaterial() == Material.GREEN_CANDLE ||
                    blockData.getMaterial() == Material.CYAN_CANDLE ||
                    blockData.getMaterial() == Material.LIGHT_BLUE_CANDLE ||
                    blockData.getMaterial() == Material.BLUE_CANDLE ||
                    blockData.getMaterial() == Material.PURPLE_CANDLE ||
                    blockData.getMaterial() == Material.MAGENTA_CANDLE ||
                    blockData.getMaterial() == Material.PINK_CANDLE ||

                    blockData.getMaterial() == Material.FLOWER_POT ||

                    blockData.getMaterial() == Material.CAULDRON ||
                    blockData.getMaterial() == Material.LAVA_CAULDRON ||
                    blockData.getMaterial() == Material.WATER_CAULDRON ||
                    blockData.getMaterial() == Material.POWDER_SNOW_CAULDRON ||

                    blockData.getMaterial() == Material.COMPOSTER ||

                    blockData.getMaterial() == Material.OAK_SIGN ||
                    blockData.getMaterial() == Material.OAK_HANGING_SIGN ||
                    blockData.getMaterial() == Material.SPRUCE_SIGN ||
                    blockData.getMaterial() == Material.SPRUCE_HANGING_SIGN ||
                    blockData.getMaterial() == Material.BIRCH_SIGN ||
                    blockData.getMaterial() == Material.BIRCH_HANGING_SIGN ||
                    blockData.getMaterial() == Material.JUNGLE_SIGN ||
                    blockData.getMaterial() == Material.JUNGLE_HANGING_SIGN ||
                    blockData.getMaterial() == Material.ACACIA_SIGN ||
                    blockData.getMaterial() == Material.ACACIA_SIGN ||
                    blockData.getMaterial() == Material.DARK_OAK_SIGN ||
                    blockData.getMaterial() == Material.DARK_OAK_HANGING_SIGN ||
                    blockData.getMaterial() == Material.MANGROVE_SIGN ||
                    blockData.getMaterial() == Material.MANGROVE_HANGING_SIGN ||
                    blockData.getMaterial() == Material.CHERRY_SIGN ||
                    blockData.getMaterial() == Material.CHERRY_HANGING_SIGN ||
                    blockData.getMaterial() == Material.BAMBOO_SIGN ||
                    blockData.getMaterial() == Material.BAMBOO_HANGING_SIGN ||
                    blockData.getMaterial() == Material.CRIMSON_SIGN ||
                    blockData.getMaterial() == Material.CRIMSON_HANGING_SIGN ||
                    blockData.getMaterial() == Material.WARPED_SIGN ||
                    blockData.getMaterial() == Material.WARPED_HANGING_SIGN ||

                    blockData.getMaterial() == Material.CHISELED_BOOKSHELF ||

                    blockData.getMaterial() == Material.CAMPFIRE ||
                    blockData.getMaterial() == Material.SOUL_CAMPFIRE ||

                    blockData.getMaterial() == Material.BEACON



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
                    blockData.getMaterial() == Material.JUKEBOX ||
                    blockData.getMaterial() == Material.NOTE_BLOCK
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
                entity instanceof ArmorStand

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
                    entity instanceof ArmorStand

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
