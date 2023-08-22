package org.tan.TownsAndNations.storage;

import org.tan.TownsAndNations.enums.TownRelation;

import java.util.HashMap;
import java.util.Map;

public class TownRelationConfirmStorage {

    private static final Map<String, HashMap<String, TownRelation>> map = new HashMap<>();

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