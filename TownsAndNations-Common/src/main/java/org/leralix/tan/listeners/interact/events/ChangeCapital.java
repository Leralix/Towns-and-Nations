package org.leralix.tan.listeners.interact.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.chunk.ClaimedChunk;
import org.leralix.tan.data.chunk.TownClaimedChunk;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.ListenerState;
import org.leralix.tan.listeners.interact.RightClickListenerEvent;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public class ChangeCapital extends RightClickListenerEvent {

    private final TownData townData;
    private final Consumer<Player> fallbackGui;

    public ChangeCapital(TownData townData, Consumer<Player> fallBackGui){
        this.townData = townData;
        this.fallbackGui = fallBackGui;
    }

    @Override
    public ListenerState execute(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();
        if(block == null) {
            return ListenerState.CONTINUE;
        }

        ClaimedChunk claimedChunk = NewClaimedChunkStorage.getInstance().get(block.getChunk());
        Player player = event.getPlayer();

        if(claimedChunk instanceof TownClaimedChunk townClaimedChunk) {

            if(townData.getID().equals(townClaimedChunk.getOwnerID())){
                townData.setCapitalLocation(claimedChunk.getVector2D());

                openGui(fallbackGui, player);
                SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                return ListenerState.SUCCESS;
            }
            TanChatUtils.message(player, Lang.CHANGE_CAPITAL_NOT_OWNED_TOWN_CHUNK.get(townClaimedChunk.getTown().getColoredName()));
            return ListenerState.FAILURE;
        }
        TanChatUtils.message(player, Lang.CHANGE_CAPITAL_NOT_TOWN_CHUNK.get());
        return ListenerState.FAILURE;
    }
}
