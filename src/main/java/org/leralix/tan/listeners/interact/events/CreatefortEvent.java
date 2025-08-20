package org.leralix.tan.listeners.interact.events;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;
import org.leralix.tan.utils.constants.Constants;

public class CreatefortEvent extends RightClickListenerEvent {

    private final TerritoryData tanTerritory;


    public CreatefortEvent(TerritoryData tanTerritory) {
        this.tanTerritory = tanTerritory;
    }

    @Override
    public void execute(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }

        World world = block.getWorld();
        Block upBlock =  world.getBlockAt(block.getX(), block.getY() + 1, block.getZ());

        if(upBlock.getType() != Material.AIR){
            player.sendMessage(TanChatUtils.getTANString() + Lang.CANNOT_CREATE_FORT_IF_ABOVE_BLOCKED.get(tanPlayer));
            return;
        }

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(upBlock.getChunk());
        if(!tanTerritory.getID().equals(claimedChunk.getOwnerID())){
            player.sendMessage(TanChatUtils.getTANString() +
                    Lang.POSITION_NOT_IN_CLAIMED_CHUNK.get(tanPlayer));
            return;
        }

        if(tanTerritory.getBalance() <= Constants.getFortCost()){
            player.sendMessage(TanChatUtils.getTANString() +
                    Lang.TERRITORY_NOT_ENOUGH_MONEY_EXTENDED.get(tanPlayer, Constants.getFortCost() - tanTerritory.getBalance()));
            return;
        }

        Vector3D position = new Vector3D(block.getLocation());

        tanTerritory.registerFort(position);
        RightClickListener.removePlayer(player);

    }


}
