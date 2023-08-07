package org.tan.towns_and_nations.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.tan.towns_and_nations.DataClass.PlayerDataClass;
import org.tan.towns_and_nations.DataClass.TownDataClass;
import org.tan.towns_and_nations.TownsAndNations;
import org.tan.towns_and_nations.enums.TownRelation;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.storage.TownDataStorage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamUtils {
    public static void setScoreBoard(Player player) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();


        for (TownRelation relation : TownRelation.values()) {
            Team team = board.registerNewTeam(relation.getName().toLowerCase());
            //team.setPrefix("" + relation.getColor() + "");
            team.setColor(relation.getColor());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            System.out.println("Setting prefix for team: " + relation.getName().toLowerCase() + " to: " + relation.getColor() + "");
        }
        player.setScoreboard(board);


        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            System.out.println("Case: " + otherPlayer.getName());
            for (TownRelation relation : TownRelation.values()) {
                System.out.println("Relation: " + relation.getName());
                if (haveRelation(player, otherPlayer, relation)) {
                    player.getScoreboard().getTeam(relation.getName().toLowerCase()).addEntry(otherPlayer.getName());
                    System.out.println("Adding " + otherPlayer.getName() + " to team " + relation.getName().toLowerCase());
                }
            }
        }
    }

    private static boolean haveRelation(Player player, Player otherPlayer, TownRelation TargetedRelation){

        TownDataClass playerTown = TownDataStorage.getTown(player);
        TownDataClass otherPlayerTown = TownDataStorage.getTown(otherPlayer);

        if(otherPlayerTown == null){
            return false;
        }

        TownRelation CurrentRelation = playerTown.getRelationWith(otherPlayerTown);

        if(CurrentRelation == null){
            return false;
        }

        return CurrentRelation == TargetedRelation;
    }


    /*
    public static void handlePlayerInfoPacket(PacketEvent event) {

        Player viewer = event.getPlayer();
        TownDataClass viewerTown = TownDataStorage.getTown(viewer);
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        /*
        PacketContainer packetBoom = manager.createPacket(PacketType.Play.Server.EXPLOSION);

        //Write the data of the packet in accordance with https://wiki.vg/Protocol
        packetBoom.getDoubles().write(0, viewer.getLocation().getX());
        packetBoom.getDoubles().write(1, viewer.getLocation().getY());
        packetBoom.getDoubles().write(2, viewer.getLocation().getZ());

        packetBoom.getFloat().write(0, 0.5f); //strength
        packetBoom.getFloat().write(1, 0.0f); //x velocity
        packetBoom.getFloat().write(2, 0.5f); //y velocity
        packetBoom.getFloat().write(3, 5.0f); //z velocity

        //send to a single player so only they see the explosion happen
        manager.sendServerPacket(viewer, packetBoom);
        //send to all players
        //manager.broadcastServerPacket(packet);

        PacketContainer packetBoom = manager.createPacket(PacketType.Play.Server.EXPLOSION);


        PacketContainer packet = event.getPacket();
        StructureModifier<List<PlayerInfoData>> playerInfoDataLists = packet.getPlayerInfoDataLists();



        System.out.println("[DEBUG] Handling PLAYER_INFO packet for " + event.getPlayer().getName());
        System.out.println("[DEBUG] Name: " + event.getPacketType().name());
        System.out.println("[DEBUG] PlayerInfoDataLists: " + playerInfoDataLists.size());


        System.out.println("[DEBUG] : " + event.getPacket().getPlayerInfoDataLists().readSafely(1).get(0));

        for(Player player : Bukkit.getOnlinePlayers()){
            UUID playerUUID = player.getUniqueId();
            sendNewInfo(viewer, playerUUID, ChatColor.GREEN);

        }









        return;

        // Modifying the list to reflect our changes
        /*
        data = data.stream().filter(Objects::nonNull).map(info -> { // Filtrer les valeurs nulles
            Player target = Bukkit.getPlayer(info.getProfile().getName());
            System.out.println("[DEBUG] Detected player: " + target.getName());

            TownDataClass targetTown = TownDataStorage.getTown(target);

            if (target != null && !viewer.equals(target)) {
                TownRelation relation = viewerTown.getRelationWith(targetTown);
                System.out.println("[DEBUG] Detected relation: " + relation.getName() + " between " + viewer.getName() + " and " + target.getName());

                if (relation != null) {
                    String prefix = "" + relation.getColor();
                    System.out.println("[DEBUG] Setting prefix: " + prefix + " for " + target.getName());

                    return new PlayerInfoData(info.getProfile(), info.getLatency(), info.getGameMode(), WrappedChatComponent.fromText(prefix + target.getName()));
                }
            }

            return info; // Return original data if no changes are made
        }).collect(Collectors.toList());


        event.getPacket().getPlayerInfoDataLists().write(0, data); // Writing our modified data back
        System.out.println("[DEBUG] Modified data written back for " + viewer.getName());

    }
    */

    /*
    public static void sendNewInfo(Player player, UUID otherPlayerUUID, ChatColor chatColor) {
        PacketContainer packet = TownsAndNations.getPlugin().protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
        System.out.println("Packet Type: " + packet.getType());
        System.out.println("Packet Class: " + packet.getHandle().getClass().getName());
        StructureModifier<Object> fields = packet.getModifier();
        System.out.println("Total fields in the packet: " + fields.size());
        for (int i = 0; i < fields.size(); i++) {
            System.out.println("Field[" + i + "]: " + fields.read(i));
        }
        System.out.println("PlayerInfoAction: " + packet.getPlayerInfoAction().read(0));
        System.out.println("PlayerInfoDataLists: " + packet.getPlayerInfoDataLists().read(0));


        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        List<PlayerInfoData> pd = new ArrayList<PlayerInfoData>();

        WrappedGameProfile profile = new WrappedGameProfile(otherPlayerUUID, chatColor + Bukkit.getPlayer(otherPlayerUUID).getDisplayName());
        WrappedChatComponent name = WrappedChatComponent.fromText(chatColor + Bukkit.getPlayer(otherPlayerUUID).getDisplayName());
        pd.add(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, name));

        packet.getPlayerInfoDataLists().write(0, pd);

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            TownsAndNations.getPlugin().protocolManager.sendServerPacket(player, packet);
        }
    }
    */

}
