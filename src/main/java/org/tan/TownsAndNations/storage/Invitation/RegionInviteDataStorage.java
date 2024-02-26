package org.tan.TownsAndNations.storage.Invitation;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionInviteDataStorage {

    private static final Map<String, ArrayList<String>> regionInviteList = new HashMap<>();

    public static void addInvitation(String playerUUID, String townId){
        if(regionInviteList.get(playerUUID) == null){
            ArrayList<String> list = new ArrayList<>();
            list.add(townId);
            regionInviteList.put(playerUUID, list);
        }
        else{
            regionInviteList.get(playerUUID).add(townId);
        }
    }


    public static void removeInvitation(String playerUUID,String townId){
        if(regionInviteList.containsKey(playerUUID)){
            regionInviteList.get(playerUUID).remove(townId);
        }
    }
    public static void removeInvitation(Player player, String townId){
        removeInvitation(player.getUniqueId().toString(),townId);
    }

    public static boolean checkInvitation(String playerUUID, String regionId){
        if(!regionInviteList.containsKey(playerUUID)){
            return false;
        }
        return regionInviteList.get(playerUUID).contains(regionId);
    }

}
