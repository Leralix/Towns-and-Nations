package org.leralix.tan.storage.invitation;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TownInviteDataStorage {

    private static final Map<String, ArrayList<String>> townInviteList = new HashMap<>();

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


    public static void removeInvitation(String playerUUID){
        townInviteList.remove(playerUUID);
    }

    public static boolean isInvited(String playerUUID,String townID){
        if(townInviteList.get(playerUUID) == null)
            return false;
        return townInviteList.get(playerUUID).contains(townID);
    }

}
