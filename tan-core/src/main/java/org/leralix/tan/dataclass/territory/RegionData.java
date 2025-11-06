package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.newhistory.SubjectTaxHistory;
import org.leralix.tan.dataclass.territory.economy.Budget;
import org.leralix.tan.dataclass.territory.economy.SubjectTaxLine;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TerritoryIndependanceInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.graphic.TeamUtils;

public class RegionData extends TerritoryData {

  private String leaderID;
  private String capitalID;
  private String nationID;
  private final List<String> townsInRegion;

  public RegionData(String id, String name, ITanPlayer owner) {
    super(id, name, owner);
    // getTownSync() retourne directement TownData (synchrone)
    TownData ownerTown = owner.getTownSync();

    this.capitalID = ownerTown.getID();
    this.nationID = null;

    this.townsInRegion = new ArrayList<>();
  }

  @Override
  protected void initUpgradesStatus() {
    this.upgradesStatus =
        new org.leralix.tan.upgrade.TerritoryStats(
            org.leralix.tan.upgrade.rewards.StatsType.REGION);
  }

  public int getHierarchyRank() {
    return 1;
  }

  @Override
  public String getBaseColoredName() {
    return "§b" + getName();
  }

  @Override
  public String getLeaderID() {
    if (leaderID == null) leaderID = getCapital().getLeaderID();
    return leaderID;
  }

  @Override
  public ITanPlayer getLeaderData() {
    return PlayerDataStorage.getInstance().getSync(getLeaderID());
  }

  @Override
  public void setLeaderID(String newLeaderID) {
    this.leaderID = newLeaderID;
  }

  @Override
  public boolean isLeader(String id) {
    return getLeaderID().equals(id);
  }

  @Override
  public Collection<String> getPlayerIDList() {
    ArrayList<String> playerList = new ArrayList<>();
    for (TerritoryData townData : getSubjects()) {
      playerList.addAll(townData.getPlayerIDList());
    }
    return playerList;
  }

  @Override
  public Collection<ITanPlayer> getITanPlayerList() {
    List<ITanPlayer> players = new ArrayList<>();
    for (String playerID : getPlayerIDList()) {
      ITanPlayer player = PlayerDataStorage.getInstance().getSync(playerID);
      if (player != null) {
        players.add(player);
      }
    }
    return players;
  }

  @Override
  public ItemStack getIconWithName() {
    ItemStack icon = getIcon();

    ItemMeta meta = icon.getItemMeta();
    if (meta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(meta, "§b" + getName());
      icon.setItemMeta(meta);
    }
    return icon;
  }

  @Override
  public ItemStack getIconWithInformations(LangType langType) {
    ItemStack icon = getIcon();

    ItemMeta meta = icon.getItemMeta();
    if (meta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(meta, "§b" + getName());

      List<String> lore = new ArrayList<>();
      lore.add(Lang.GUI_REGION_INFO_DESC0.get(langType, getDescription()));
      lore.add(Lang.GUI_REGION_INFO_DESC1.get(langType, getCapital().getName()));
      lore.add(Lang.GUI_REGION_INFO_DESC2.get(langType, Integer.toString(getNumberOfTownsIn())));
      lore.add(Lang.GUI_REGION_INFO_DESC3.get(langType, Integer.toString(getTotalPlayerCount())));
      lore.add(
          Lang.GUI_REGION_INFO_DESC5.get(langType, Integer.toString(getNumberOfClaimedChunk())));

      org.leralix.tan.utils.text.ComponentUtil.setLore(meta, lore);
      icon.setItemMeta(meta);
    }
    return icon;
  }

  public int getTotalPlayerCount() {
    int count = 0;
    for (TerritoryData town : getSubjects()) {
      count += town.getPlayerIDList().size();
    }
    return count;
  }

  @Override
  public boolean haveOverlord() {
    return nationID != null;
  }

  @Override
  public void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {

    removeFromBalance(getClaimCost());
    NewClaimedChunkStorage.getInstance().claimRegionChunk(chunk, getID());
  }

  @Override
  protected Collection<TerritoryData> getOverlords() {
    return new ArrayList<>();
  }

  public List<TerritoryData> getSubjects() {
    List<TerritoryData> towns = new ArrayList<>();
    for (String townID : townsInRegion) {
      towns.add(TerritoryUtil.getTerritory(townID));
    }
    return towns;
  }

  public int getNumberOfTownsIn() {
    return townsInRegion.size();
  }

  @Override
  protected void addVassalPrivate(TerritoryData vassal) {
    townsInRegion.add(vassal.getID());
  }

  public void setCapital(String townID) {
    this.capitalID = townID;
  }

  @Override
  public void removeOverlordPrivate() {
    // Kingdoms are not implemented yet
  }

  @Override
  protected void collectTaxes() {
    for (TerritoryData town : getVassals()) {
      if (town == null) continue;
      double tax = getTax();
      if (town.getBalance() < tax) {
        TownsAndNations.getPlugin()
            .getDatabaseHandler()
            .addTransactionHistory(new SubjectTaxHistory(this, town, -1));
      } else {
        town.removeFromBalance(tax);
        addToBalance(tax);
        TownsAndNations.getPlugin()
            .getDatabaseHandler()
            .addTransactionHistory(new SubjectTaxHistory(this, town, tax));
      }
    }
  }

  @Override
  protected void removeVassal(TerritoryData vassal) {

    EventManager.getInstance().callEvent(new TerritoryIndependanceInternalEvent(this, vassal));

    townsInRegion.remove(vassal.getID());

    TownData town = (TownData) vassal;

    for (RankData rank : getRanks().values()) {
      for (String playerID : town.getPlayerIDList()) {
        rank.removePlayer(playerID);
      }
    }
  }

  @Override
  public TerritoryData getCapital() {
    if (capitalID == null) {
      capitalID = getSubjects().getFirst().getID();
    }
    return TerritoryUtil.getTerritory(capitalID);
  }

  @Override
  public void broadCastMessage(FilledLang message) {
    for (TerritoryData townData : getSubjects()) townData.broadCastMessage(message);
  }

  @Override
  public void broadcastMessageWithSound(
      FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
    for (TerritoryData townData : getSubjects()) {
      townData.broadcastMessageWithSound(message, soundEnum, addPrefix);
    }
  }

  @Override
  public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
    for (TerritoryData townData : getSubjects()) {
      townData.broadcastMessageWithSound(message, soundEnum);
    }
  }

  @Override
  public boolean haveNoLeader() {
    return false; // Region always have a leader
  }

  @Override
  public synchronized void delete() {
    super.delete();

    TeamUtils.updateAllScoreboardColor();
    RegionDataStorage.getInstance().deleteRegion(this);
  }

  @Override
  public void openMainMenu(Player player) {
    PlayerGUI.dispatchPlayerRegion(player);
  }

  @Override
  public boolean canHaveVassals() {
    return true;
  }

  @Override
  public boolean canHaveOverlord() {
    return true;
  }

  @Override
  public List<String> getVassalsID() {
    return townsInRegion;
  }

  @Override
  public boolean isVassal(String territoryID) {
    return townsInRegion.contains(territoryID);
  }

  @Override
  public Collection<TerritoryData> getPotentialVassals() {
    return new ArrayList<>(TownDataStorage.getInstance().getAllAsync().join().values());
  }

  @Override
  public RankData getRank(ITanPlayer tanPlayer) {
    if (!tanPlayer.hasRegion()) {
      return null;
    }
    return getRank(tanPlayer.getRegionRankID());
  }

  @Override
  public List<GuiItem> getOrderedMemberList(ITanPlayer tanPlayer) {
    List<GuiItem> res = new ArrayList<>();
    for (String playerUUID : getOrderedPlayerIDListSync()) {
      OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
      ITanPlayer playerIterateData = PlayerDataStorage.getInstance().getSync(playerUUID);
      ItemStack playerHead =
          HeadUtils.getPlayerHead(
              playerIterate,
              Lang.GUI_TOWN_MEMBER_DESC1.get(
                  tanPlayer.getLang(), playerIterateData.getRegionRank().getColoredName()));

      GuiItem playerButton =
          ItemBuilder.from(playerHead).asGuiItem(event -> event.setCancelled(true));
      res.add(playerButton);
    }
    return res;
  }

  @Override
  protected void specificSetPlayerRank(ITanPlayer playerStat, int rankID) {
    playerStat.setRegionRankID(rankID);
  }

  @Override
  protected void addSpecificTaxes(Budget budget) {
    budget.addProfitLine(new SubjectTaxLine(this));
  }
}
