package org.tan.TownsAndNations.listeners;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.SoundUtil;

import static org.tan.TownsAndNations.enums.SoundEnum.BAD;
import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class PlayerEnterChunkListener implements Listener {

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){


        Chunk currentChunk = e.getFrom().getChunk();
        if(e.getTo() == null){
            return;
        }
        Chunk nextChunk = e.getTo().getChunk();

        if(currentChunk.equals(nextChunk)){
            return;
        }

        if(!ClaimedChunkStorage.isChunkClaimed(currentChunk) && !ClaimedChunkStorage.isChunkClaimed(nextChunk)){
            return;
        }


        TownData townFrom = ClaimedChunkStorage.getChunkOwnerTown(currentChunk);
        TownData townTo = ClaimedChunkStorage.getChunkOwnerTown(nextChunk);


        if(equalsWithNulls(townFrom,townTo)){
            return;
        }

        Player player = e.getPlayer();

        if(townFrom != null){
            player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_ENTER_WILDERNESS.getTranslation());
            return;
        }

        player.sendMessage(ChatUtils.getTANString() + Lang.CHUNK_ENTER_TOWN.getTranslation(townTo.getName()));

        TownData playerTown = TownDataStorage.get(player);
        if(playerTown == null){
            return;
        }
        TownRelation relation = TownDataStorage.get(player).getRelationWith(townTo);

        if(relation == TownRelation.WAR){
            SoundUtil.playSound(player, BAD);
            player.sendMessage(Lang.CHUNK_ENTER_TOWN_AT_WAR.getTranslation());

            townTo.broadCastMessageWithSound(Lang.CHUNK_INTRUSION_ALERT.getTranslation(TownDataStorage.get(player).getName(),player.getName()),
                    BAD);
        }




    }

    public static final boolean equalsWithNulls(Object a, Object b) {
        if (a==b) return true;
        if ((a==null)||(b==null)) return false;
        return a.equals(b);
    }


}
