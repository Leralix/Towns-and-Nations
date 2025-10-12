package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ActiveTruce;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TruceStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.RelationConstant;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public class RemoveRelationMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final TownRelation relation;

    public RemoveRelationMenu(Player player, TerritoryData territoryData, TownRelation relation){
        super(player, Lang.HEADER_SELECT_REMOVE_TERRITORY_RELATION.get(player, relation.getName(PlayerDataStorage.getInstance().get(player).getLang())), 6);
        this.territoryData = territoryData;
        this.relation = relation;
        open();
    }

    @Override
    public void open() {
        iterator(getTerritories(), p -> new OpenRelationMenu(player, territoryData, relation), Material.RED_STAINED_GLASS_PANE);

        gui.open(player);
    }

    private List<GuiItem> getTerritories() {
        List<String> relationListID = territoryData.getRelations().getTerritoriesIDWithRelation(relation);
        List<GuiItem> guiItems = new ArrayList<>();


        for (String otherTownUUID : relationListID) {
            TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
            ItemStack townIcon = otherTerritory.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang());

            GuiItem townGui = ItemBuilder.from(townIcon).asGuiItem(event -> {
                event.setCancelled(true);

                if (relation.isSuperiorTo(TownRelation.NEUTRAL)) {

                    RelationConstant relationConstant = Constants.getRelationConstants(relation);
                    int trucePeriod = relationConstant.trucePeriod();
                    if(trucePeriod > 0){
                        ActiveTruce activeTruce = new ActiveTruce(territoryData, otherTerritory, trucePeriod);
                        TruceStorage.getInstance().add(activeTruce);
                    }

                    territoryData.setRelation(otherTerritory, TownRelation.NEUTRAL);
                } else {
                    otherTerritory.receiveDiplomaticProposal(territoryData, TownRelation.NEUTRAL);
                    TanChatUtils.message(player, Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(tanPlayer, otherTerritory.getName()), MINOR_GOOD);
                }
                new OpenRelationMenu(player, territoryData, relation);
            });
            guiItems.add(townGui);
        }
        return guiItems;
    }


}
