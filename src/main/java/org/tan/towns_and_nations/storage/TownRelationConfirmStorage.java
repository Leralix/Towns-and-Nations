package org.tan.towns_and_nations.storage;

import org.tan.towns_and_nations.enums.TownRelation;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.HashMap;

public class TownRelationConfirmStorage {

    public static HashMap<String, HashMap<String, TownRelation>> map = new HashMap<>();

    public static void addInvitation(String playerUUID, String townId, TownRelation relation) {

        if (!map.containsKey(playerUUID)) {
            map.put(playerUUID, new HashMap<>());
        }
        map.get(playerUUID).put(townId, relation);
    }


    public static void removeInvitation(String playerUUID,String townId){
        map.get(playerUUID).remove(townId);
    }

    public static TownRelation getRelation(String playerUUID, String townId){
        return map.get(playerUUID).get(townId);
    }

    public static boolean checkInvitation(String playerUUID,String townId){
        return map.get(playerUUID).containsKey(townId);
    }

}