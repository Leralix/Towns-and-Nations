package org.leralix.tan.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.StringUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceHolderAPI extends PlaceholderExpansion {

    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private static final Lang INVALID_VALUE = Lang.INVALID_VALUE;
    private static final Lang INVALID_ID = Lang.INVALID_ID;
    private static final Lang INVALID_NAME = Lang.INVALID_NAME;
    private static final Lang INVALID_TERRITORY = Lang.INVALID_TERRITORY;
    private static final Lang INVALID_PLAYER_NAME = Lang.INVALID_PLAYER_NAME;

    PlayerDataStorage playerManager = PlayerDataStorage.getInstance();

    @Override
    @NotNull
    public String getAuthor() {
        return "Leralix";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "tan";
    }

    @Override
    @NotNull
    public String getVersion() {
        return TownsAndNations.getPlugin().getCurrentVersion().toString();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        ITanPlayer tanPlayer = playerManager.get(player.getUniqueId());

        if (tanPlayer == null) {
            return "Data not found"; // Gérer le cas où les données du joueur ne sont pas trouvées
        }

        if (params.equalsIgnoreCase("player_balance")) {
            String moneyChar = EconomyUtil.getMoneyIcon();
            return StringUtil.formatMoney(EconomyUtil.getBalance(player)) + moneyChar;
        }
        if (params.equalsIgnoreCase("player_town_name")) {
            return tanPlayer.hasTown() ? tanPlayer.getTown().getName() : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_town_resident_quantity")) {
            return tanPlayer.hasTown() ? Integer.toString(tanPlayer.getTown().getPlayerIDList().size()) : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_town_chunk_actual_quantity")) {
            return tanPlayer.hasTown() ? Integer.toString(tanPlayer.getTown().getNumberOfClaimedChunk()) : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_town_chunk_max_quantity")) {
            return tanPlayer.hasTown() ? Integer.toString(tanPlayer.getTown().getLevel().getChunkCap()) : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_town_chunk_remaining_quantity")) {
            if(!tanPlayer.hasTown()) return Lang.NO_TOWN.get(tanPlayer);
            int remaining = tanPlayer.getTown().getLevel().getChunkCap() - tanPlayer.getTown().getNumberOfClaimedChunk();
            return Integer.toString(remaining);
        }
        else if (params.equalsIgnoreCase("player_town_balance")) {
            return tanPlayer.hasTown() ? Double.toString(tanPlayer.getTown().getBalance()) : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_town_rank_name")) {
            return tanPlayer.hasTown() ? tanPlayer.getTownRank().getName() : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_town_rank_colored_name")) {
            return tanPlayer.hasTown() ? tanPlayer.getTownRank().getColoredName() : Lang.NO_TOWN.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_region_name")) {
            return tanPlayer.hasRegion() ? tanPlayer.getRegion().getName() : Lang.NO_REGION.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_region_resident_quantity")) {
            return tanPlayer.hasRegion() ? Integer.toString(tanPlayer.getRegion().getTotalPlayerCount()) : Lang.NO_REGION.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_region_chunk_actual_quantity")) {
            return tanPlayer.hasRegion() ? Integer.toString(tanPlayer.getRegion().getNumberOfClaimedChunk()) : Lang.NO_REGION.get(tanPlayer);
        }
        else if (params.equalsIgnoreCase("player_region_balance")) {
            return tanPlayer.hasRegion() ? Double.toString(tanPlayer.getRegion().getBalance()) : Lang.NO_REGION.get(tanPlayer);
        }
        else if (params.startsWith("server_get_first_territory_id_with_name_")){
            String name = extractValues(params)[0];
            if(name == null) return INVALID_NAME.get(tanPlayer);
            TerritoryData territoryData = getTerritoryByName(name);
            if(territoryData == null) return INVALID_TERRITORY.get(tanPlayer);
            return territoryData.getID();
        }
        else if(params.startsWith("territory_with_id_{") && params.endsWith("}_exist")){
            String[] values = extractValues(params);
            if(values.length == 0) return INVALID_VALUE.get(tanPlayer);
            String id = values[0];
            if(id == null) return INVALID_ID.get(tanPlayer);
            return TownDataStorage.getInstance().get(id) != null || RegionDataStorage.getInstance().get(id) != null ? TRUE : FALSE;
        }
        else if(params.startsWith("territory_with_name_{") && params.endsWith("}_exist")){
            String[] values = extractValues(params);
            if(values.length == 0) return INVALID_VALUE.get(tanPlayer);
            String name = values[0];
            if(name == null) return INVALID_NAME.get(tanPlayer);

            return getTerritoryByName(name) != null ? TRUE : FALSE;
        }
        else if(params.startsWith("territory_with_id_{") && params.endsWith("}_leader_name")){
            String[] values = extractValues(params);
            if(values.length == 0) return INVALID_VALUE.get(tanPlayer);
            String id = values[0];
            if(id == null) return INVALID_ID.get(tanPlayer);
            TerritoryData territoryData = TownDataStorage.getInstance().get(id);
            if(territoryData == null) territoryData = RegionDataStorage.getInstance().get(id);
            if (territoryData == null) return INVALID_TERRITORY.get(tanPlayer);

            return territoryData.getLeaderData().getOfflinePlayer().getName();
        }
        else if(params.startsWith("territory_with_name_{") && params.endsWith("}_leader_name")){
            String[] values = extractValues(params);
            if(values.length == 0) return INVALID_VALUE.get(tanPlayer);
            String name = values[0];
            if(name == null) return INVALID_ID.get(tanPlayer);
            TerritoryData territoryData = getTerritoryByName(name);
            if (territoryData == null) return INVALID_TERRITORY.get(tanPlayer);
            return territoryData.getLeaderData().getOfflinePlayer().getName();
        }
        else if(params.startsWith("player_{") && params.endsWith("}_have_town")){
            String[] values = extractValues(params);
            if(values.length == 0) return INVALID_VALUE.get(tanPlayer);
            String playerName = values[0];
            if(playerName == null) return INVALID_PLAYER_NAME.get(tanPlayer);
            OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(playerName);
            ITanPlayer tanPlayer1 = playerManager.get(playerSelected);
            if(tanPlayer1 == null) return INVALID_NAME.get(tanPlayer);
            return tanPlayer1.hasTown() ? TRUE: FALSE;
        }
        else if(params.startsWith("player_{") && params.endsWith("}_is_town_overlord")){
            String[] values = extractValues(params);
            if(values.length == 0) return INVALID_VALUE.get(tanPlayer);
            String playerName = values[0];
            if(playerName == null) return INVALID_PLAYER_NAME.get(tanPlayer);
            OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(playerName);
            ITanPlayer tanPlayer1 = playerManager.get(playerSelected);
            if(tanPlayer1 == null) return INVALID_NAME.get(tanPlayer);
            return tanPlayer1.isTownOverlord() ? TRUE: FALSE;
        }
        else if(params.equals("chat_mode")){
            return LocalChatStorage.getPlayerChatScope(player.getUniqueId().toString()).getName();
        }
        else if(params.startsWith("chat_mode_{") && params.endsWith("}")){
            String[] values = extractValues(params);
            OfflinePlayer playerSelected = Bukkit.getOfflinePlayer(values[0]);
            if(!playerSelected.isOnline())
                return INVALID_PLAYER_NAME.get(tanPlayer);
            return LocalChatStorage.getPlayerChatScope(playerSelected.getUniqueId().toString()).getName();
        }
        else if(params.startsWith("player_bigger_overlord_name")){
            if(tanPlayer.hasRegion())
                return tanPlayer.getRegion().getName();
            if(tanPlayer.hasTown())
                return tanPlayer.getTown().getName();
            return Lang.NO_TOWN.get(tanPlayer);
        }

        return null;
    }

    private TerritoryData getTerritoryByName(String name) {
        for(TownData townData : TownDataStorage.getInstance().getAll()){
            if(townData.getName().equalsIgnoreCase(name)){
                return townData;
            }
        }
        for(RegionData regionData : RegionDataStorage.getInstance().getAll()){
            if(regionData.getName().equalsIgnoreCase(name)){
                return regionData;
            }
        }
        return null;
    }

    public static String[] extractValues(String input) {
        Pattern pattern = Pattern.compile("\\{(.*?)}");
        Matcher matcher = pattern.matcher(input);

        ArrayList<String> values = new ArrayList<>();

        while (matcher.find()) {
            values.add(matcher.group(1));
        }

        return values.toArray(new String[0]);
    }


}
