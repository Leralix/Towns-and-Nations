package org.leralix.tan.gui.user.ranks;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class ManageRankPermissionMenu extends IteratorGUI {


    private final TerritoryData territoryData;
    private final RankData rankData;

    public ManageRankPermissionMenu(Player player, TerritoryData territoryData, RankData rankData){
        super(player, Lang.HEADER_RANK_PERMISSIONS, 4);
        this.territoryData = territoryData;
        this.rankData = rankData;
        open();
    }


    @Override
    public void open() {
        iterator(getItems(), p -> new RankManagerMenu(player, territoryData, rankData));

        gui.open(player);
    }

    public List<GuiItem> getItems() {
        List<GuiItem> guiItems = new ArrayList<>();
        for(RolePermission permission : RolePermission.values()){
            if(permission.isForTerritory(territoryData)){
                guiItems.add(iconManager.get(permission.getIconKey())
                        .setName(permission.getName().get(tanPlayer))
                        .setDescription(
                                rankData.hasPermission(permission) ?
                                        Lang.GUI_TOWN_MEMBERS_ROLE_HAS_PERMISSION.get() :
                                        Lang.GUI_TOWN_MEMBERS_ROLE_NO_PERMISSION.get())
                        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                        .setAction(event -> {
                            event.setCancelled(true);
                            if(!territoryData.getRank(player).hasPermission(permission) && !territoryData.isLeader(player)) {
                                TanChatUtils.message(player, Lang.ERROR_CANNOT_CHANGE_PERMISSION_IF_PLAYER_RANK_DOES_NOT_HAVE_IT.get(player), SoundEnum.NOT_ALLOWED);
                                return;
                            }
                            rankData.switchPermission(permission);
                            open();
                        })
                        .asGuiItem(player, langType));
            }
        }
        return guiItems;
    }

}
