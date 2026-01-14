package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class OpenRelationMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final TownRelation relation;

    public OpenRelationMenu(Player player, TerritoryData territoryData, TownRelation relation) {
        super(player, Lang.HEADER_RELATION_WITH.get(relation.getName(PlayerDataStorage.getInstance().get(player).getLang())), 6);
        this.territoryData = territoryData;
        this.relation = relation;
        open();
    }

    @Override
    public void open() {

        iterator(getTerritories(), p -> new OpenDiplomacyMenu(player, territoryData));

        gui.setItem(6, 4, getRemoveTerritoryButton());
        gui.setItem(6, 5, getAddTerritoryButton());

        gui.open(player);
    }

    private GuiItem getRemoveTerritoryButton() {
        return iconManager.get(IconKey.GUI_REMOVE_TERRITORY_ICON)
                .setName(Lang.GUI_TERRITORY_RELATION_REMOVE_TERRITORY.get(tanPlayer))
                .setAction(
                        action -> {
                            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TOWN_RELATION)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                                return;
                            }
                            new RemoveRelationMenu(player, territoryData, relation);
                        })
                .asGuiItem(player, langType);
    }

    private GuiItem getAddTerritoryButton() {
        return iconManager.get(IconKey.GUI_ADD_TERRITORY_ICON)
                .setName(Lang.GUI_TERRITORY_RELATION_ADD_TERRITORY.get(tanPlayer))
                .setAction(
                        action -> {
                            if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TOWN_RELATION)) {
                                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                                return;
                            }
                            new AddRelationMenu(player, territoryData, relation);
                        })
                .asGuiItem(player, langType);
    }

    private List<GuiItem> getTerritories() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (String territoryID : territoryData.getRelations().getTerritoriesIDWithRelation(relation)) {

            TerritoryData otherTerritory = TerritoryUtil.getTerritory(territoryID);
            if(otherTerritory == null){
                continue;
            }

            IconBuilder icon = otherTerritory.getIconWithInformationAndRelation(territoryData, langType);
            guiItems.add(icon.asGuiItem(player, langType));
        }
        return guiItems;
    }
}
