package org.tan.TownsAndNations.storage;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.PropertyData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.DataClass.Vector3D;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.GUI.GuiManager2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.SoundUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSelectPropertyPositionStorage {

    private static final Map<String, List<Vector3D>> playerList = new HashMap<>();

    public static boolean contains(Player player){
        return contains(player.getUniqueId().toString());
    }
    public static boolean contains(PlayerData playerData){
        return contains(playerData.getID());
    }
    public static boolean contains(String playerID){
        return playerList.containsKey(playerID);
    }
    public static void addPlayer(String playerID){
        playerList.put(playerID, new ArrayList<>());
    }

    public static void addPlayer(PlayerData playerData){
        addPlayer(playerData.getID());
    }

    public static void removePlayer(Player player){
        playerList.remove(player.getUniqueId().toString());
    }
    public static void removePlayer(String playerID){
        playerList.remove(playerID);
    }

    public static void addPoint(Player player, Block block){
        String playerID = player.getUniqueId().toString();
        PlayerData playerData = PlayerDataStorage.get(playerID);
        TownData playerTown = playerData.getTown();

        ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.get(block.getChunk());
        if(claimedChunk == null){
            player.sendMessage(Lang.POSITION_NOT_IN_CLAIMED_CHUNK.get());
        }

        List<Vector3D> vList = playerList.get(playerID);
        if(vList.isEmpty()){
            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());
            vList.add(vector3D);
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_FIRST_POINT_SET.get(vector3D));
        }
        else if(vList.size() == 1) {

            int maxPropertySize = ConfigUtil.getCustomConfig("config.yml").getInt("maxPropertySize", 50000);
            if(Math.abs(vList.get(0).getX() - block.getX()) * Math.abs(vList.get(0).getY() - block.getY()) * Math.abs(vList.get(0).getZ() - block.getZ()) > maxPropertySize){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_PROPERTY_TOO_BIG.get(maxPropertySize));
                return;
            }


            Vector3D vector3D = new Vector3D(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID().toString());
            vList.add(vector3D);
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_SECOND_POINT_SET.get(vector3D));
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_PLACE_SIGN.get());
        }
        else if(vList.size() == 2){
            int margin = ConfigUtil.getCustomConfig("config.yml").getInt("maxPropertyMargin",3);
            if(!isNearProperty(block.getLocation(),vList.get(0),vList.get(1), margin)){
                player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_PROPERTY_SIGN_TOO_FAR.get(margin));
                return;
            }

            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
            player.sendMessage(ChatUtils.getTANString() + Lang.PLAYER_PROPERTY_CREATED.get());
            removePlayer(playerID);

            PropertyData property = playerTown.registerNewProperty(vList.get(0),vList.get(1),playerData);
            GuiManager2.OpenPropertyManagerMenu(player,property);
            createPropertyPanel(player, property, block);
            property.updateSign();
        }
    }

    public static void createPropertyPanel(Player player, PropertyData propertyData, Block block) {
        Location signLocation = block.getLocation().add(0, 1, 0);
        //if (signLocation.getBlock().getType() == Material.AIR) {
        signLocation.getBlock().setType(Material.OAK_SIGN);

        BlockState blockState = signLocation.getBlock().getState();
        Sign sign = (Sign) blockState;
        org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) sign.getBlockData();
        BlockFace direction = getDirection(block.getLocation(), player.getLocation());
        signData.setRotation(direction);
        sign.setBlockData(signData);
        sign.update();

        block.setMetadata("propertySign", new FixedMetadataValue(TownsAndNations.getPlugin(), propertyData.getTotalID()));
        signLocation.getBlock().setMetadata("propertySign", new FixedMetadataValue(TownsAndNations.getPlugin(), propertyData.getTotalID()));
        //}
        propertyData.setSignLocation(signLocation);
    }

    private static BlockFace getDirection(Location blockLocation, Location playerLocation) {
        double dx = playerLocation.getX() - blockLocation.getX();
        double dz = playerLocation.getZ() - blockLocation.getZ();
        double angle = Math.toDegrees(Math.atan2(dz, dx)) + 180;
        return getClosestCardinalDirection(angle);
    }

    // Méthode pour obtenir la direction cardinale la plus proche
    private static BlockFace getClosestCardinalDirection(double angle) {
        if (angle < 45 || angle >= 315) {
            return BlockFace.WEST;
        } else if (angle >= 45 && angle < 135) {
            return BlockFace.NORTH;
        } else if (angle >= 135 && angle < 225) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }


    private static String getCardinalDirection(double dx, double dz) {
        double angle = Math.toDegrees(Math.atan2(dz, dx));
        if (angle < 0) {
            angle += 360;
        }
        if (angle >= 315 || angle < 45) {
            return "South";
        } else if (angle < 135) {
            return "West";
        } else if (angle < 225) {
            return "North";
        } else {
            return "East";
        }
    }

    static boolean isNearProperty(Location blockLocation,Vector3D p1, Vector3D p2,  int margin) {
        double minX = Math.min(p1.getX(), p2.getX()) - margin;
        double minY = Math.min(p1.getY(), p2.getY()) - margin;
        double minZ = Math.min(p1.getZ(), p2.getZ()) - margin;
        double maxX = Math.max(p1.getX(), p2.getX()) + margin;
        double maxY = Math.max(p1.getY(), p2.getY()) + margin;
        double maxZ = Math.max(p1.getZ(), p2.getZ()) + margin;

        double blockX = blockLocation.getX();
        double blockY = blockLocation.getY();
        double blockZ = blockLocation.getZ();

        return blockX >= minX && blockX <= maxX &&
                blockY >= minY && blockY <= maxY &&
                blockZ >= minZ && blockZ <= maxZ;
    }
}
