package org.tan.towns_and_nations.storage;

import org.tan.towns_and_nations.enums.TownRelation;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.HashMap;

public class TownRelationConfirmStorage {

    public static HashMap<String, HashMap<String, TownRelation>> List = new HashMap<>();

    public static void addInvitation(String playerUUID, String townId, TownRelation relation) {

        if (!List.containsKey(playerUUID)) {
            List.put(playerUUID, new HashMap<>());
        List.get(playerUUID).put(townId, relation);

        }
    }


    public static void removeInvitation(String playerUUID,String townId){
        List.get(playerUUID).remove(townId);
    }

    public static TownRelation getRelation(String playerUUID, String townId){
        return List.get(playerUUID).get(townId);
    }

    public static boolean checkInvitation(String playerUUID,String townId){
        return List.get(playerUUID).get(townId) != null;
    }

}