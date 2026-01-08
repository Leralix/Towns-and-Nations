package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ActiveTruce;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.storage.stored.TruceStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.RelationConstant;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class AddRelationMenu extends IteratorGUI {

    private final TerritoryData territoryData;
    private final TownRelation wantedRelation;


    public AddRelationMenu(Player player, TerritoryData territory, TownRelation wantedRelation) {
        super(player, Lang.HEADER_SELECT_ADD_TERRITORY_RELATION.get(wantedRelation.getName(PlayerDataStorage.getInstance().get(player).getLang())), 6);
        this.territoryData = territory;
        this.wantedRelation = wantedRelation;
        open();
    }

    @Override
    public void open() {

        iterator(getTerritories(), p -> new OpenRelationMenu(player, territoryData, wantedRelation), Material.GREEN_STAINED_GLASS_PANE);

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

            if (otherTerritory == null) {
                continue;
            }

            TownRelation actualRelation = territoryData.getRelationWith(otherTerritory);

            if (!actualRelation.canBeChanged()) {
                continue;
            }

            guiItems.add(otherTerritory.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang())
                    .setAction(action -> {
                        if (otherTerritory.haveNoLeader()) {
                            TanChatUtils.message(player, Lang.TERRITORY_DIPLOMATIC_INVITATION_NO_LEADER.get(tanPlayer));
                            return;
                        }

                        if (wantedRelation.isSuperiorTo(actualRelation)) {
                            otherTerritory.receiveDiplomaticProposal(territoryData, wantedRelation);
                            TanChatUtils.message(player, Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(tanPlayer, otherTerritory.getName()));
                        } else {
                            RelationConstant relationConstant = Constants.getRelationConstants(actualRelation);
                            int trucePeriod = relationConstant.trucePeriod();
                            //If actual relation has a truce, it cannot be switched to a negative relation instantly
                            if (wantedRelation.isNegative()) {
                                if (trucePeriod > 0) {
                                    TanChatUtils.message(player, Lang.CURRENT_RELATION_REQUIRES_TRUCE.get(tanPlayer, Integer.toString(trucePeriod)), NOT_ALLOWED);
                                    return;
                                }
                                long nbActiveHourTruce = TruceStorage.getInstance().getRemainingTruce(territoryData, otherTerritory);
                                if (nbActiveHourTruce > 0) {
                                    TanChatUtils.message(player, Lang.CANNOT_SET_RELATION_TO_NEGATIVE_WHILE_TRUCE.get(tanPlayer, Long.toString(nbActiveHourTruce), otherTerritory.getColoredName()), NOT_ALLOWED);
                                    return;
                                }
                            }

                            //Successfully switched to a new relation. If old relation required a truce, apply it.
                            ActiveTruce activeTruce = new ActiveTruce(territoryData, otherTerritory, trucePeriod);
                            TruceStorage.getInstance().add(activeTruce);
                            territoryData.setRelation(otherTerritory, wantedRelation);
                        }
                        new OpenRelationMenu(player, territoryData, wantedRelation);
                    })
                    .asGuiItem(player, langType)
            );
        }

        return guiItems;
    }
}
