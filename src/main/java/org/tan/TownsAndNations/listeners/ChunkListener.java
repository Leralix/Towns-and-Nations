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
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
            if(blockData.getMaterial() == Material.CHEST||
                    blockData.getMaterial() == Material.TRAPPED_CHEST){


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
            else if(blockData.getMaterial() == Material.OAK_DOOR ||
                    blockData.getMaterial() == Material.SPRUCE_DOOR ||
                    blockData.getMaterial() == Material.ACACIA_DOOR ||
                    blockData.getMaterial() == Material.DARK_OAK_DOOR ||
                    blockData.getMaterial() == Material.BAMBOO_DOOR ||
                    blockData.getMaterial() == Material.BIRCH_DOOR ||
                    blockData.getMaterial() == Material.CRIMSON_DOOR ||
                    blockData.getMaterial() == Material.JUNGLE_DOOR ||
                    blockData.getMaterial() == Material.WARPED_DOOR ||
                    blockData.getMaterial() == Material.IRON_DOOR){

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

            if(entity instanceof Cow || entity instanceof Chicken || entity instanceof Sheep ||
                    entity instanceof Donkey || entity instanceof Cat || entity instanceof SkeletonHorse ||
                    entity instanceof Axolotl || entity instanceof Golem || entity instanceof Rabbit ||
                    entity instanceof WanderingTrader || entity instanceof Fish || entity instanceof Mule ||
                    entity instanceof Turtle || entity instanceof Villager || entity instanceof ArmorStand) {

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
