package org.leralix.tan.storage.invitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TownInviteDataStorage {

    private static final Map<UUID, ArrayList<String>> townInviteList = new HashMap<>();

    public static void addInvitation(UUID playerID, String townId){
        if(townInviteList.get(playerID) == null){
            ArrayList<String> list = new ArrayList<>();
            list.add(townId);
            townInviteList.put(playerID, list);
        }
        else{
            townInviteList.get(playerID).add(townId);
        }
    }


    public static void removeInvitation(UUID playerUUID){
        townInviteList.remove(playerUUID);
    }

    public static boolean isInvited(UUID playerUUID, String townID){
        if(townInviteList.get(playerUUID) == null)
            return false;
        return townInviteList.get(playerUUID).contains(townID);
    }

}
