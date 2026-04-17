package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.truce.ActiveTruce;
import org.leralix.tan.storage.stored.truce.TruceStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.RelationConstant;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;

public class RemoveRelationMenu extends IteratorGUI {

    private final Territory territoryData;
    private final TownRelation relation;

    public RemoveRelationMenu(Player player, Territory territoryData, TownRelation relation){
        super(player, Lang.HEADER_SELECT_REMOVE_TERRITORY_RELATION.get(relation.getName(TownsAndNations.getPlugin().getPlayerDataStorage().get(player).getLang())), 6);
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
        List<GuiItem> guiItems = new ArrayList<>();


        for (Territory otherTerritory : territoryData.getRelations().getTerritoriesWithRelation(relation)) {
            guiItems.add(otherTerritory.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang())
                    .setAction(event -> {
                        event.setCancelled(true);

                        if(TownsAndNations.getPlugin().getWarStorage().isTerritoryAtWarWith(territoryData, otherTerritory)){
                            TanChatUtils.message(player, Lang.CANNOT_REMOVE_RELATION_WAR.get(tanPlayer, otherTerritory.getName()), MINOR_GOOD);
                            return;
                        }

                        if (relation.isSuperiorTo(TownRelation.NEUTRAL)) {

                            RelationConstant relationConstant = Constants.getRelationConstants(relation);
                            int trucePeriod = relationConstant.trucePeriod();
                            if(trucePeriod > 0){
                                ActiveTruce activeTruce = new ActiveTruce(territoryData, otherTerritory, trucePeriod);
                                TruceStorage.getInstance().add(activeTruce);
                            }

                            TerritoryUtil.setRelation(territoryData, otherTerritory, TownRelation.NEUTRAL);
                        } else {
                            otherTerritory.addDiplomaticProposal(territoryData, TownRelation.NEUTRAL);
                            TanChatUtils.message(player, Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(tanPlayer, otherTerritory.getName()), MINOR_GOOD);
                        }
                        new OpenRelationMenu(player, territoryData, relation);
                    })
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }


}
