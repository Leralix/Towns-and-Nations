package org.leralix.tan.listeners.interact.events;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.WildernessChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreatefortEvent extends RightClickListenerEvent {

    private final TerritoryData tanTerritory;


    public CreatefortEvent(TerritoryData tanTerritory) {
        this.tanTerritory = tanTerritory;
    }

    @Override
    public boolean execute(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        Block block = event.getClickedBlock();
        if(block == null) {
            return false;
        }

        World world = block.getWorld();
        Block upBlock =  world.getBlockAt(block.getX(), block.getY() + 1, block.getZ());

        if(upBlock.getType() != Material.AIR){
            player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_CREATE_FORT_IF_ABOVE_BLOCKED.get(tanPlayer));
            return false;
        }

        if(tanTerritory.getBalance() <= Constants.getFortCost()){
            player.sendMessage(TanChatUtils.getTANString() +
                    Lang.TERRITORY_NOT_ENOUGH_MONEY_EXTENDED.get(tanPlayer, Constants.getFortCost() - tanTerritory.getBalance()));
            return false;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(upBlock.getChunk());

        // If outposts are enabled and chunk is not claimed
        if(Constants.enableFortOutpost() && claimedChunk instanceof WildernessChunk){
            boolean wasAbleToClaim = tanTerritory.claimChunk(player, upBlock.getChunk(), true);

            if(!wasAbleToClaim){
                return false;
            }
        }
        // Else, only create a fort if created inside a claimed chunk
        else {
            if(!tanTerritory.getID().equals(claimedChunk.getOwnerID())){
                player.sendMessage(TanChatUtils.getTANString() + Lang.POSITION_NOT_IN_CLAIMED_CHUNK.get(tanPlayer));
                return false;
            }
        }
        createFort(block);
        return true;
    }

    private void createFort(Block block) {
        Vector3D position = new Vector3D(block.getLocation());
        tanTerritory.registerFort(position);
    }


}
