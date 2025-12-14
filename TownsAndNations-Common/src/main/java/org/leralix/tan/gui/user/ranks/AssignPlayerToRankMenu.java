package org.leralix.tan.gui.user.ranks;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AssignPlayerToRankMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final RankData rankData;

    public AssignPlayerToRankMenu(Player player, TerritoryData territoryData, RankData rankData){
        super(player, Lang.HEADER_RANK_ADD_PLAYER.get(), 3);
        this.territoryData = territoryData;
        this.rankData = rankData;

    }

    @Override
    public void open() {
        GuiUtil.createIterator(gui, getAvailablePlayers(), page, player,
                p -> new RankManagerMenu(player, territoryData, rankData),
                p -> nextPage(),
                p -> previousPage());

        gui.open(player);
    }

    private List<GuiItem> getAvailablePlayers() {
        List<GuiItem> playersToAdd = new ArrayList<>();
        for (String otherPlayerUUID : territoryData.getPlayerIDList()) {
            ITanPlayer otherITanPlayer = PlayerDataStorage.getInstance().get(otherPlayerUUID);

            if(Objects.equals(otherITanPlayer.getRankID(territoryData), rankData.getID())){
                continue;
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(otherPlayerUUID));
            playersToAdd.add(iconManager.get(offlinePlayer)
                    .setName(offlinePlayer.getName())
                    .setAction(action -> {
                        action.setCancelled(true);
                        RankData otherPlayerActualRank = territoryData.getRank(otherITanPlayer);
                        if(territoryData.getRank(player).getLevel() <= otherPlayerActualRank.getLevel() && !territoryData.isLeader(tanPlayer)){
                            TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(tanPlayer));
                            return;
                        }

                        ITanPlayer playerStat = PlayerDataStorage.getInstance().get(otherPlayerUUID);
                        territoryData.setPlayerRank(playerStat, rankData);
                        new RankManagerMenu(player, territoryData, rankData).open();
                    })
                    .asGuiItem(player, langType)
            );
        }
        return playersToAdd;
    }
}
