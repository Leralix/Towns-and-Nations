package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateRank;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class TerritoryRanksMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public TerritoryRanksMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_TERRITORY_RANKS, 4);
        this.territoryData = territoryData;
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getRanks(), page, player,
                p -> territoryData.openMainMenu(player),
                p -> nextPage(),
                p -> previousPage()
        );

        gui.setItem(4, 5, getCreateNewRoleButton());

        gui.open(player);
    }

    private GuiItem getCreateNewRoleButton() {

        int nbRanks = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("townMaxRank",8);

        return iconManager.get(IconKey.NEW_RANK_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES.get(playerData))
                .setDescription(
                        Lang.GUI_TOWN_MEMBERS_ADD_NEW_ROLES_DESC1.get(playerData, nbRanks),
                        Lang.GUI_GENERIC_CLICK_TO_CREATE.get(playerData)
                )
                .setAction( event -> {
                    event.setCancelled(true);

                    if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.CREATE_RANK)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        return;
                    }
                    if(territoryData.getNumberOfRank() >= nbRanks){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_RANK_CAP_REACHED.get(playerData));
                        return;
                    }
                    player.sendMessage(TanChatUtils.getTANString() + Lang.WRITE_IN_CHAT_NEW_ROLE_NAME.get(playerData));
                    PlayerChatListenerStorage.register(player, new CreateRank(territoryData, p -> new TerritoryRanksMenu(player, territoryData).open()));
                })
                .asGuiItem(player);
    }

    private List<GuiItem> getRanks() {
        List<GuiItem> res = new ArrayList<>();
        for (RankData rank: territoryData.getAllRanksSorted()) {
            res.add(getSingleRankButton(rank));
        }
        return res;
    }

    private GuiItem getSingleRankButton(RankData rank) {
        return iconManager.get(rank.getRankIcon())
                .setName(rank.getColoredName())
                .setDescription(
                        Lang.GUI_RANK_NUMBER_PLAYER_WITH_ROLE.get(playerData, rank.getNumberOfPlayer()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get()
                )
                .setAction(action -> {
                    if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_RANKS)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(playerData));
                        return;
                    }
                    if(territoryData.getRank(playerData).getLevel() <= rank.getLevel() && !territoryData.isLeader(playerData)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(playerData));
                        return;
                    }
                    new RankManagerMenu(player, territoryData, rank).open();
                })
                .asGuiItem(player);
    }
}
