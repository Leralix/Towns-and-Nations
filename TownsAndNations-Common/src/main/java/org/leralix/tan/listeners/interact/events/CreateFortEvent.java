package org.leralix.tan.listeners.interact.events;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.chunk.IClaimedChunk;
import org.leralix.tan.data.chunk.TerritoryChunkData;
import org.leralix.tan.data.chunk.WildernessChunkData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.ListenerState;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.utils.constants.BlockList;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateFortEvent extends RightClickListenerEvent {

    private final Territory tanTerritory;
    private final ITanPlayer tanPlayer;

    public CreateFortEvent(Territory tanTerritory, ITanPlayer tanPlayer) {
        this.tanTerritory = tanTerritory;
        this.tanPlayer = tanPlayer;
    }

    @Override
    public ListenerState execute(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        if (block == null) {
            return ListenerState.CONTINUE;
        }

        if(TownsAndNations.getPlugin().getFortStorage().getFortOnChunk(block.getLocation().getChunk()).isPresent()){
            TanChatUtils.message(tanPlayer, Lang.FORT_ALREADY_PRESENT_ON_CHUNK);
            return ListenerState.CONTINUE;
        }


        if(BlockList.BREAKABLE_BLOCKS.contains(block.getType())){
            TanChatUtils.message(tanPlayer, Lang.CANNOT_CREATE_FORT_ON_THIS_BLOCK);
            return ListenerState.CONTINUE;
        }

        World world = block.getWorld();
        Block upBlock = world.getBlockAt(block.getX(), block.getY() + 1, block.getZ());

        if (upBlock.getType() != Material.AIR) {
            TanChatUtils.message(tanPlayer, Lang.CANNOT_CREATE_FORT_IF_ABOVE_BLOCKED);
            return ListenerState.FAILURE;
        }

        if (tanTerritory.getBalance() <= Constants.getFortCost()) {
            TanChatUtils.message(tanPlayer, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanTerritory.getColoredName(), Double.toString(Constants.getFortCost() - tanTerritory.getBalance())));
            return ListenerState.FAILURE;
        }

        Chunk chunk = upBlock.getChunk();
        IClaimedChunk claimedChunk = TownsAndNations.getPlugin().getClaimStorage().get(chunk);


        switch (claimedChunk){
            case WildernessChunkData ignored when Constants.enableFortOutpost() -> {

                boolean wasAbleToClaim = tanTerritory.claimChunk(event.getPlayer(), tanPlayer, chunk, true);

                if (wasAbleToClaim) {
                    createFort(block);
                    return ListenerState.SUCCESS;
                }
                else {
                    return ListenerState.FAILURE;
                }
            }
            case TerritoryChunkData territoryChunk -> {
                if (territoryChunk.getOwnerID().equals(tanTerritory.getID())) {
                    createFort(block);
                    return ListenerState.SUCCESS;
                }
                else {
                    TanChatUtils.message(tanPlayer, Lang.POSITION_NOT_IN_CLAIMED_CHUNK);
                    return ListenerState.FAILURE;
                }
            }
            default -> {
                return ListenerState.FAILURE;
            }
        }
    }

    private void createFort(Block block) {
        Vector3D position = new Vector3D(block.getLocation());
        Fort fort = TownsAndNations.getPlugin().getFortStorage().register(position, tanTerritory);
        fort.spawnFlag();
        tanTerritory.addOwnedFort(fort);
    }


}
