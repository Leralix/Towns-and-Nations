package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public abstract class AbstractTransaction {

    protected LocalDateTime localDate;

    protected AbstractTransaction(){
        this.localDate = LocalDateTime.now();
    }

    protected AbstractTransaction(LocalDateTime localDate){
        this.localDate = localDate;
    }

    public LocalDateTime getDate(){
        return localDate;
    }

    public abstract TransactionType getType();

    public abstract GuiItem getIcon(IconManager iconManager, Player player, LangType langType);

    public abstract String getInsertSQL();

    protected String getPlayerName(String playerID){
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
        return ChatColor.BLUE + offlinePlayer.getName();
    }

    protected String getTerritoryName(String id) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(id);
        if(territoryData == null){
            return ChatColor.RED + "Deleted town";
        }
        return territoryData.getColoredName();
    }

    /**
     * This method is used with any ID. It will try to know the type of ID before.
     * @param id    The ID of the entity to display name
     * @return  The name of the ID with the correct color code.
     */
    protected @NotNull String getColoredName(String id){
        if(id.startsWith("T") || id.startsWith("R")){
            return getTerritoryName(id);
        }
        else {
            return getPlayerName(id);
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

    public abstract List<String> getConcerned();
}
