package org.leralix.tan.listeners.interact.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TownClaimedChunk;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;

import java.util.function.Consumer;

public class ChangeCapital extends RightClickListenerEvent {

    private final TownData townData;
    private final Consumer<Player> fallbackGui;

    public ChangeCapital(TownData townData, Consumer<Player> fallBackGui){
        this.townData = townData;
        this.fallbackGui = fallBackGui;
    }

    @Override
    public void execute(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        if(block == null) {
            return;
        }

        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(block.getChunk());

        if(claimedChunk2 instanceof TownClaimedChunk townClaimedChunk &&
                townData.getID().equals(townClaimedChunk.getOwnerID())){
            townData.setCapitalLocation(claimedChunk2.getVector2D());

            Player player = event.getPlayer();

            openGui(fallbackGui, player);
            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
            RightClickListener.removePlayer(player);
        }
    }
}
