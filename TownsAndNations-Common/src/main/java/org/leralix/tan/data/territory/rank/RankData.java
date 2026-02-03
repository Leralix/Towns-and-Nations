package org.leralix.tan.data.territory.rank;

import org.bukkit.inventory.ItemStack;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.cosmetic.CustomIcon;
import org.leralix.tan.storage.stored.PlayerDataStorage;

import java.util.*;

public class RankData {

    private Integer ID;
    private String name;
    private RankEnum rankEnum;
    CustomIcon rankIcon;
    private final List<UUID> players;
    private int salary;
    private final Set<RolePermission> permissions = EnumSet.noneOf(RolePermission.class);

    private boolean isPayingTaxes;

    public RankData(int id, String name) {
        this.ID = id;
        this.name = name;
        this.rankEnum = RankEnum.FIVE;
        this.players = new ArrayList<>();
        this.isPayingTaxes = true;
        this.salary = 0;
    }

    public void swapPayingTaxes() {
        this.isPayingTaxes = !this.isPayingTaxes;
    }

    public String getName() {
        return this.name;
    }

    public String getColoredName() {
        return this.rankEnum.getColor() + this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public RankEnum getRankEnum() {
        return this.rankEnum;
    }

    public int getLevel() {
        return this.rankEnum.getLevel();
    }

    public void incrementLevel() {
        this.rankEnum = rankEnum.nextRank();
    }

    public void decrementLevel() {
        this.rankEnum = rankEnum.previousRank();
    }

    public ItemStack getRankIcon() {
        if (this.rankIcon == null)
            return rankEnum.getBasicRankIcon();
        return rankIcon.getIcon();
    }

    public void addPlayer(UUID playerID) {
        this.players.add(playerID);
    }

    public void addPlayer(ITanPlayer tanPlayer) {
        addPlayer(tanPlayer.getID());
    }

    public void removePlayer(UUID playerID) {
        this.players.remove(playerID);
    }

    public void removePlayer(ITanPlayer player) {
        removePlayer(player.getID());
    }

    public List<UUID> getPlayersID() {
        return this.players;
    }

    public List<ITanPlayer> getPlayers() {
        List<ITanPlayer> playerList = new ArrayList<>();
        for (UUID playerID : this.players) {
            playerList.add(PlayerDataStorage.getInstance().get(playerID));
        }
        return playerList;
    }

    public boolean isPayingTaxes() {
        return this.isPayingTaxes;
    }

    public void setRankIcon(ItemStack rankItem) {
        this.rankIcon = new CustomIcon(rankItem);
    }

    public int getNumberOfPlayer() {
        return players.size();
    }

    public void addPermission(RolePermission permission) {
        permissions.add(permission);
    }

    /**
     * Check if the rank has a specific permission
     * Keep in mind that since leader overrides all permissions,
     * {@link TerritoryData#checkPlayerPermission} should be used for player permission
     * @param permission    the permission to check
     * @return  True if the rank contains the permission, false otherwise.
     */
    public boolean hasPermission(RolePermission permission) {
        return permissions.contains(permission);
    }

    public void removePermission(RolePermission permission) {
        permissions.remove(permission);
    }

    public void switchPermission(RolePermission permission) {
        if (hasPermission(permission))
            removePermission(permission);
        else
            addPermission(permission);

    }


    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void addFromSalary(int amount) {
        this.salary += amount;
    }

    public void removeFromSalary(int amount) {
        this.salary -= amount;
    }

    public int getSalary() {
        return this.salary;
    }


    public Integer getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public boolean isSuperiorTo(RankData rank) {
        return this.getRankEnum().getLevel() > rank.getRankEnum().getLevel();
    }


}
