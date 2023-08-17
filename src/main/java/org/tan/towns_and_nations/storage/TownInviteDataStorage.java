package org.tan.towns_and_nations.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TownInviteDataStorage {

    public static Map<String, ArrayList<String>> townInviteList = new HashMap<>();

    public static void addInvitation(String playerUUID, String townId){
        if(townInviteList.get(playerUUID) == null){
            ArrayList<String> list = new ArrayList<>();
            list.add(townId);
            townInviteList.put(playerUUID, list);
        }
        else{
            townInviteList.get(playerUUID).add(townId);
        }
    }


    public static void removeInvitation(String playerUUID,String townId){
        townInviteList.get(playerUUID).remove(townId);
    }

    public static List<String> checkInvitation(String playerUUID){
        return townInviteList.get(playerUUID);
    }

}
