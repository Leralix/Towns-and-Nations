package org.tan.towns_and_nations.storage;

import java.util.ArrayList;
import java.util.HashMap;

public class TownInviteDataStorage {

    public static HashMap<String, ArrayList<String>> townInviteList = new HashMap<>();


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


    public static void removeInvitation(String playerName){
        townInviteList.get(playerName).remove(playerName);
    }

    public static ArrayList<String> checkInvitation(String playerName){
        return townInviteList.get(playerName);
    }




}
