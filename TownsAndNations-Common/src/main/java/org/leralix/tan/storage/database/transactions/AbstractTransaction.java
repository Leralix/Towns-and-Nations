package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.NewUpgradeStorage;
import org.leralix.tan.upgrade.Upgrade;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractTransaction {

    protected long localDate;

    protected AbstractTransaction(){
        this.localDate = new Date().getTime();
    }

    protected AbstractTransaction(long timestamp) {
        this.localDate = timestamp;
    }

    public long getDate(){
        return localDate;
    }

    public abstract TransactionType getType();

    public abstract GuiItem getIcon(IconManager iconManager, Player player, LangType langType);

    public abstract String getInsertSQL();

    protected String getPlayerName(String playerID, LangType langType){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
        String playerName = offlinePlayer.getName();
        if(playerName == null){
            return Lang.PLAYER_NOT_FOUND.get(langType);
        }
        return offlinePlayer.getName();
    }

    protected String getTerritoryName(String id, LangType langType) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(id);
        if(territoryData == null){
            return Lang.TERRITORY_NOT_FOUND.get(langType);
        }
        return territoryData.getColoredName();
    }

    protected String getPropertyName(String territoryID, String propertyID, LangType langType) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
        if(territoryData == null){
            return Lang.TERRITORY_NOT_FOUND.get(langType);
        }
        // As of 0.16.1, Region canot have properties.
        if(territoryData instanceof TownData townData){
            PropertyData propertyData = townData.getPropertyDataMap().get(propertyID);
            if(propertyData == null){
                return Lang.PROPERTY_NOT_FOUND.get(langType);
            }
            return propertyData.getName();
        }
        return Lang.PROPERTY_NOT_FOUND.get(langType);
    }

    protected String getUpgradeName(String territoryID, String upgradeID, LangType langType) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(territoryID);
        NewUpgradeStorage upgradeStorage = Constants.getUpgradeStorage();
        if(territoryData == null){
            return Lang.TERRITORY_NOT_FOUND.get(langType);
        }
        Upgrade upgrade = upgradeStorage.getUpgrade(territoryData, upgradeID);
        if(upgrade == null){
            return Lang.UPGRADE_NOT_FOUND.get(langType);
        }
        return upgrade.getName(langType);
    }

    /**
     * This method is used with any ID. It will try to know the type of ID before.
     * @param id    The ID of the entity to display name
     * @return  The name of the ID with the correct color code.
     */
    protected @NotNull String getColoredName(String id, LangType langType){
        if(id.startsWith("T") || id.startsWith("R")){
            return getTerritoryName(id, langType);
        }
        else {
            return getPlayerName(id, langType);
        }
    }



    /** Factory method for reflective instantiation */
    public static <T extends AbstractTransaction> T fromResultSet(ResultSet rs, Class<T> clazz) throws SQLException {
        try {
            var constructor = clazz.getDeclaredConstructor(ResultSet.class);
            return constructor.newInstance(rs);
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to instantiate " + clazz.getSimpleName() + " (" + e.getMessage() + ")", e);
        }
    }

    public abstract void fillInsertStatement(PreparedStatement ps) throws SQLException;

    public abstract Set<String> getConcerned();
}
