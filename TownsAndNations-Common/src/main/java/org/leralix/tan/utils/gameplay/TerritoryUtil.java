package org.leralix.tan.utils.gameplay;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DiplomacyProposalAcceptedInternalEvent;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.utils.graphic.TeamUtils;

import java.util.*;

public class TerritoryUtil {

    private TerritoryUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Territory getTerritory(String id){
        if(id.startsWith("T")) {
            return TownsAndNations.getPlugin().getTownStorage().get(id);
        }
        if(id.startsWith("R")) {
            return TownsAndNations.getPlugin().getRegionStorage().get(id);
        }
        if (id.startsWith("N")) {
            return TownsAndNations.getPlugin().getNationStorage().get(id);
        }
        return null;
    }

    public static boolean isNameUsed(String name, Collection<? extends Territory> territories){
        String territoryName = name.replaceAll(" ", "-");
        for(Territory territory : territories){
            if(territoryName.equals(territory.getName().replaceAll(" ", "-"))){
                return true;
            }
        }
        return false;
    }

    public static @NotNull List<Territory> getTerritories(BrowseScope scope) {
        List<Territory> territoryList = new ArrayList<>();

        if(scope == BrowseScope.ALL || scope == BrowseScope.TOWNS)
            territoryList.addAll(TownsAndNations.getPlugin().getTownStorage().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.REGIONS)
            territoryList.addAll(TownsAndNations.getPlugin().getRegionStorage().getAll().values());
        if(scope == BrowseScope.ALL || scope == BrowseScope.NATIONS)
            territoryList.addAll(TownsAndNations.getPlugin().getNationStorage().getAll().values());
        return territoryList;
    }

    public static Set<Territory> getTerritoriesAuthorizingTeleportation(ITanPlayer tanPlayer) {

        List<Territory> allTerritories = TerritoryUtil.getTerritories(BrowseScope.ALL);
        Set<Territory> authorizedTerritories = new HashSet<>();

        for (Territory territoryData : tanPlayer.getAllTerritoriesPlayerIsIn()) {
            for (Territory iterateTerritory : allTerritories) {
                if (!authorizedTerritories.contains(iterateTerritory)
                        && iterateTerritory.authorizeTeleportation(territoryData)
                        && iterateTerritory.getTeleportationData().isSpawnSet()
                ) {
                    authorizedTerritories.add(iterateTerritory);
                }
            }
        }
        return authorizedTerritories;
    }

    public static void setRelation(Territory proposingTerritory, Territory acceptingTerritory, TownRelation newRelation) {
        TownRelation oldRelation = proposingTerritory.getRelationWith(acceptingTerritory);

        if(oldRelation == newRelation) {
            return;
        }

        proposingTerritory.setRelation(newRelation, acceptingTerritory.getID());
        acceptingTerritory.setRelation(newRelation, proposingTerritory.getID());

        EventManager.getInstance().callEvent(new DiplomacyProposalAcceptedInternalEvent(proposingTerritory, acceptingTerritory, oldRelation, newRelation));
        TeamUtils.updateAllScoreboardColor();
    }

}
