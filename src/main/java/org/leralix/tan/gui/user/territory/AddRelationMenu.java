package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;

public class AddRelationMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final TownRelation wantedRelation;


    public AddRelationMenu(Player player, TerritoryData territory, TownRelation wantedRelation) {
        super(player, Lang.HEADER_SELECT_ADD_TERRITORY_RELATION.get(player, wantedRelation.getName()), 6);
        this.territoryData = territory;
        this.wantedRelation = wantedRelation;
        open();
    }

    @Override
    public void open() {

        iterator(getTerritories(), p -> PlayerGUI.openSingleRelation(player, territoryData, wantedRelation, 0));

        gui.open(player);
    }

    private List<GuiItem> getTerritories() {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        List<String> relationListID = territoryData.getRelations().getTerritoriesIDWithRelation(wantedRelation);
        List<GuiItem> guiItems = new ArrayList<>();

        List<String> territories = new ArrayList<>();
        territories.addAll(TownDataStorage.getInstance().getAll().keySet());
        territories.addAll(RegionDataStorage.getInstance().getAll().keySet());

        territories.removeAll(relationListID); //Territory already have this relation
        territories.remove(territoryData.getID()); //Remove itself

        for (String otherTownUUID : territories) {
            TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
            ItemStack icon = otherTerritory.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang());

            TownRelation actualRelation = territoryData.getRelationWith(otherTerritory);

            if (!actualRelation.canBeChanged()) {
                continue;
            }

            GuiItem iconGui = ItemBuilder.from(icon).asGuiItem(event -> {
                event.setCancelled(true);

                if (otherTerritory.haveNoLeader()) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_DIPLOMATIC_INVITATION_NO_LEADER.get(tanPlayer));
                    return;
                }

                if (wantedRelation.isSuperiorTo(actualRelation)) {
                    otherTerritory.receiveDiplomaticProposal(territoryData, wantedRelation);
                    player.sendMessage(TanChatUtils.getTANString() + Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(tanPlayer, otherTerritory.getName()));
                } else {
                    territoryData.setRelation(otherTerritory, wantedRelation);
                }
                PlayerGUI.openSingleRelation(player, territoryData, wantedRelation, 0);

            });
            guiItems.add(iconGui);
        }

        return guiItems;
    }
}
