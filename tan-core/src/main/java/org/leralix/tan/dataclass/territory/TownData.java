package org.leralix.tan.dataclass.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.position.Vector2D;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.*;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.newhistory.PlayerTaxHistory;
import org.leralix.tan.dataclass.territory.economy.*;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.PlayerJoinTownAcceptedInternalEvent;
import org.leralix.tan.events.events.PlayerJoinTownRequestInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.user.territory.TerritoryMemberMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.*;
import org.leralix.tan.upgrade.rewards.numeric.TownPlayerCap;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.graphic.PrefixUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class TownData extends TerritoryData {

  // This is all that should be kept after the transition to the parent class
  private String uuidLeader;
  private String townTag;
  private boolean isRecruiting;
  private HashSet<String> playerJoinRequestSet;
  private Map<String, PropertyData> propertyDataMap;
  private TeleportationPosition teleportationPosition;
  private final HashSet<String> townPlayerListId;
  private Vector2D capitalLocation;

  public TownData(String townId, String townName, ITanPlayer leader) {
    super(townId, townName, leader);
    this.playerJoinRequestSet = new HashSet<>();
    this.townPlayerListId = new HashSet<>();
    this.isRecruiting = false;

    if (leader != null) {
      this.uuidLeader = leader.getID();
      addPlayer(leader);
    }

    int prefixSize = Constants.getPrefixSize();
    this.townTag =
        townName.length() >= prefixSize
            ? townName.substring(0, prefixSize).toUpperCase()
            : townName.toUpperCase();
  }

  @Override
  protected void initUpgradesStatus() {
    this.upgradesStatus =
        new org.leralix.tan.upgrade.TerritoryStats(org.leralix.tan.upgrade.rewards.StatsType.TOWN);
  }

  @Override
  public RankData getRank(ITanPlayer tanPlayer) {
    return getRank(tanPlayer.getTownRankID());
  }

  public void addPlayer(ITanPlayer tanNewPlayer) {
    townPlayerListId.add(tanNewPlayer.getID());
    getTownDefaultRank().addPlayer(tanNewPlayer);
    tanNewPlayer.joinTown(this);

    // All player interactions MUST run on main thread
    org.leralix.tan.utils.FoliaScheduler.runTask(
        org.leralix.tan.TownsAndNations.getPlugin(),
        () -> {
          TanChatUtils.message(
              tanNewPlayer.getPlayer(),
              Lang.TOWN_INVITATION_ACCEPTED_MEMBER_SIDE.get(
                  tanNewPlayer.getLang(), getBaseColoredName()));

          tanNewPlayer.clearAllTownApplications();

          for (TerritoryData overlords : getOverlords()) {
            overlords.registerPlayer(tanNewPlayer);
          }

          EventManager.getInstance()
              .callEvent(new PlayerJoinTownAcceptedInternalEvent(tanNewPlayer, this));
          TeamUtils.updateAllScoreboardColor();
          PrefixUtil.updatePrefix(tanNewPlayer.getPlayer());
        });

    // Save this town to database (DatabaseStorage auto-saves on put)
    TownDataStorage.getInstance().putSync(getID(), this);
  }

  public void removePlayer(String tanPlayerID) {
    removePlayer(PlayerDataStorage.getInstance().getSync(tanPlayerID));
  }

  public void removePlayer(ITanPlayer tanPlayer) {
    for (TerritoryData overlords : getOverlords()) {
      overlords.unregisterPlayer(tanPlayer);
    }

    getRank(tanPlayer).removePlayer(tanPlayer);
    townPlayerListId.remove(tanPlayer.getID());
    tanPlayer.leaveTown();
    // Save this town to database (DatabaseStorage auto-saves on put)
    TownDataStorage.getInstance().putSync(getID(), this);
    PrefixUtil.updatePrefix(tanPlayer.getPlayer());
  }

  @Override
  public Collection<String> getPlayerIDList() {
    return townPlayerListId;
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
    ItemStack itemStack = getIcon();

    ItemMeta meta = itemStack.getItemMeta();
    if (meta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(meta, "§a" + getName());
      itemStack.setItemMeta(meta);
    }
    return itemStack;
  }

  @Override
  public ItemStack getIconWithInformations(LangType langType) {
    ItemStack icon = getIcon();

    ItemMeta meta = icon.getItemMeta();
    if (meta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(meta, "§a" + getName());

      List<String> lore = new ArrayList<>();
      lore.add(Lang.GUI_TOWN_INFO_DESC0.get(langType, getDescription()));
      lore.add(Lang.GUI_TOWN_INFO_DESC1.get(langType, getLeaderNameSync()));
      lore.add(Lang.GUI_TOWN_INFO_DESC2.get(langType, Integer.toString(getPlayerIDList().size())));
      lore.add(Lang.GUI_TOWN_INFO_DESC3.get(langType, Integer.toString(getNumberOfClaimedChunk())));
      lore.add(
          getOverlord()
              .map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(langType, overlord.getName()))
              .orElseGet(() -> Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(langType)));

      org.leralix.tan.utils.text.ComponentUtil.setLore(meta, lore);
      icon.setItemMeta(meta);
    }
    return icon;
  }

  @Override
  public int getHierarchyRank() {
    return 0;
  }

  @Override
  public String getBaseColoredName() {
    return "§9" + getName();
  }

  @Override
  public String getLeaderID() {
    if (this.uuidLeader == null) {
      if (townPlayerListId.isEmpty()) {
        return null; // No leader and no players in the list
      }
      return townPlayerListId
          .iterator()
          .next(); // If the leader is null, the first player in the list is the leader
    }
    return this.uuidLeader;
  }

  @Override
  public ITanPlayer getLeaderData() {
    String leaderID = getLeaderID();
    if (leaderID == null) {
      return null;
    }
    return PlayerDataStorage.getInstance().getSync(leaderID);
  }

  @Override
  public void setLeaderID(String leaderID) {
    this.uuidLeader = leaderID;
  }

  @Override
  public boolean isLeader(String leaderID) {
    return getLeaderID().equals(leaderID);
  }

  @Override
  protected Collection<TerritoryData> getOverlords() {
    List<TerritoryData> overlords = new ArrayList<>();

    if (haveOverlord()) {
      RegionData regionData = RegionDataStorage.getInstance().getSync(this.overlordID);
      if (regionData != null) {
        overlords.add(regionData);
        regionData.getOverlord().ifPresent(overlords::add);
      }
    }

    return overlords;
  }

  @Override
  public void broadCastMessage(FilledLang message) {
    for (String playerId : townPlayerListId) {
      Player player = Bukkit.getServer().getPlayer(UUID.fromString(playerId));
      if (player != null && player.isOnline()) {

        TanChatUtils.message(player, message.get(player));
      }
    }
  }

  @Override
  public void broadcastMessageWithSound(
      FilledLang message, SoundEnum soundEnum, boolean addPrefix) {
    for (String playerId : townPlayerListId) {
      org.leralix.tan.utils.FoliaScheduler.runTask(
          org.leralix.tan.TownsAndNations.getPlugin(),
          () -> {
            Player player = Bukkit.getPlayer(UUID.fromString(playerId));
            if (player != null && player.isOnline()) {
              TanChatUtils.message(player, message, soundEnum);
            }
          });
    }
  }

  @Override
  public void broadcastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
    broadcastMessageWithSound(message, soundEnum, true);
  }

  public RankData getTownDefaultRank() {
    return getRank(getDefaultRankID());
  }

  public boolean isFull() {
    return !upgradesStatus.getStat(TownPlayerCap.class).canDoAction(this.townPlayerListId.size());
  }

  public void addPlayerJoinRequest(Player player) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    EventManager.getInstance().callEvent(new PlayerJoinTownRequestInternalEvent(tanPlayer, this));
    addPlayerJoinRequest(tanPlayer.getID());
  }

  public void addPlayerJoinRequest(String playerUUID) {
    this.playerJoinRequestSet.add(playerUUID);
  }

  public void removePlayerJoinRequest(String playerUUID) {
    playerJoinRequestSet.remove(playerUUID);
  }

  public void removePlayerJoinRequest(Player player) {
    removePlayerJoinRequest(player.getUniqueId().toString());
  }

  public boolean isPlayerAlreadyRequested(String playerUUID) {
    return playerJoinRequestSet.contains(playerUUID);
  }

  public boolean isPlayerAlreadyRequested(Player player) {
    return isPlayerAlreadyRequested(player.getUniqueId().toString());
  }

  public Set<String> getPlayerJoinRequestSet() {
    return this.playerJoinRequestSet;
  }

  public boolean isRecruiting() {
    return isRecruiting;
  }

  public void swapRecruiting() {
    this.isRecruiting = !this.isRecruiting;
  }

  @Override
  protected void collectTaxes() {
    Collection<ITanPlayer> tanPlayers = getITanPlayerList();
    for (ITanPlayer tanPlayer : tanPlayers) {
      OfflinePlayer offlinePlayer = tanPlayer.getOfflinePlayer();

      // Bloquant ici pour getRank, mais c'est une opération rapide sur l'objet ITanPlayer
      if (!getRank(tanPlayer).isPayingTaxes()) continue;

      double tax = getTax();

      if (EconomyUtil.getBalance(offlinePlayer) > tax) {
        EconomyUtil.removeFromBalance(offlinePlayer, tax);
        addToBalance(tax);
        TownsAndNations.getPlugin()
            .getDatabaseHandler()
            .addTransactionHistory(new PlayerTaxHistory(this, tanPlayer, tax));
      } else {
        TownsAndNations.getPlugin()
            .getDatabaseHandler()
            .addTransactionHistory(new PlayerTaxHistory(this, tanPlayer, -1));
      }
    }
  }

  public void setSpawn(Location location) {
    this.teleportationPosition = new TeleportationPosition(location);
  }

  public boolean isSpawnSet() {
    return this.teleportationPosition != null;
  }

  public TeleportationPosition getSpawn() {
    return this.teleportationPosition;
  }

  @Override
  public void abstractClaimChunk(Player player, Chunk chunk, boolean ignoreAdjacent) {

    removeFromBalance(getClaimCost());
    NewClaimedChunkStorage.getInstance()
        .unclaimChunkAndUpdate(NewClaimedChunkStorage.getInstance().get(chunk));

    ClaimedChunk2 chunkClaimed =
        NewClaimedChunkStorage.getInstance().claimTownChunk(chunk, getID());
    // If this was the first claimed chunk, set the capital.
    if (getNumberOfClaimedChunk() == 1) {
      setCapitalLocation(chunkClaimed.getVector2D());
    }
  }

  public void setCapitalLocation(Vector2D vector2D) {
    capitalLocation = vector2D;
  }

  public Optional<Vector2D> getCapitalLocation() {
    return Optional.ofNullable(capitalLocation);
  }

  public RegionData getRegionSync() {
    return RegionDataStorage.getInstance().getSync(this.overlordID);
  }

  @Override
  public Collection<TerritoryData> getPotentialVassals() {
    return Collections.emptyList();
  }

  public void removeOverlordPrivate() {
    Collection<ITanPlayer> tanPlayers = getITanPlayerList();
    for (ITanPlayer tanPlayer : tanPlayers) {
      tanPlayer.setRegionRankID(null);
    }
  }

  @Override
  protected void addVassalPrivate(TerritoryData vassal) {
    // town have no vassals
  }

  @Override
  protected void removeVassal(TerritoryData vassal) {
    // Town have no vassals
  }

  @Override
  public TerritoryData getCapital() {
    return null;
  }

  @Override
  public List<GuiItem> getOrderedMemberList(ITanPlayer tanPlayer) {
    Player player = tanPlayer.getPlayer();
    List<GuiItem> res = new ArrayList<>();
    LangType langType = tanPlayer.getLang();

    for (String playerUUID : getOrderedPlayerIDListSync()) {
      OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
      ITanPlayer playerIterateData = PlayerDataStorage.getInstance().getSync(playerUUID);
      ItemStack playerHead =
          HeadUtils.getPlayerHead(
              playerIterate,
              Lang.GUI_TOWN_MEMBER_DESC1.get(
                  langType, playerIterateData.getTownRank().getColoredName()),
              Lang.GUI_TOWN_MEMBER_DESC2.get(
                  langType, StringUtil.formatMoney(EconomyUtil.getBalance(playerIterate))),
              doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)
                  ? Lang.GUI_TOWN_MEMBER_DESC3.get(langType)
                  : "");

      GuiItem playerButton =
          ItemBuilder.from(playerHead)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);
                    if (event.getClick() == ClickType.RIGHT) {

                      ITanPlayer kickedPlayer =
                          PlayerDataStorage.getInstance().getSync(playerIterate);
                      TownData townData =
                          TownDataStorage.getInstance().getSync(tanPlayer.getTownId());

                      if (!doesPlayerHavePermission(tanPlayer, RolePermission.KICK_PLAYER)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType));
                        return;
                      }
                      if (townData
                          .getRank(kickedPlayer)
                          .isSuperiorTo(townData.getRank(tanPlayer))) {
                        TanChatUtils.message(
                            player, Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(langType));
                        return;
                      }
                      if (isLeader(kickedPlayer)) {
                        TanChatUtils.message(
                            player, Lang.GUI_TOWN_MEMBER_CANT_KICK_LEADER.get(langType));
                        return;
                      }
                      if (tanPlayer.getID().equals(kickedPlayer.getID())) {
                        TanChatUtils.message(
                            player, Lang.GUI_TOWN_MEMBER_CANT_KICK_YOURSELF.get(langType));
                        return;
                      }

                      PlayerGUI.openConfirmMenu(
                          player,
                          Lang.CONFIRM_PLAYER_KICKED.get(langType, playerIterate.getName()),
                          confirmAction -> {
                            kickPlayer(playerIterate);
                            new TerritoryMemberMenu(player, this).open();
                          },
                          p -> new TerritoryMemberMenu(player, this).open());
                    }
                  });
      res.add(playerButton);
    }
    return res;
  }

  @Override
  protected void specificSetPlayerRank(ITanPlayer tanPlayer, int rankID) {
    tanPlayer.setTownRankID(rankID);
  }

  @Override
  protected void addSpecificTaxes(Budget budget) {
    budget.addProfitLine(new PlayerTaxLine(this));
    getOverlord().ifPresent(overlord -> budget.addProfitLine(new OverlordTaxLine(this, overlord)));
    budget.addProfitLine(new PropertyRentTaxLine(this));
    budget.addProfitLine(new PropertySellTaxLine(this));
    budget.addProfitLine(new PropertyCreationTaxLine(this));
  }

  public Map<String, PropertyData> getPropertyDataMap() {
    if (this.propertyDataMap == null) {
      synchronized (this) {
        if (this.propertyDataMap == null) {
          this.propertyDataMap = new HashMap<>();
        }
      }
    }
    return this.propertyDataMap;
  }

  public Collection<PropertyData> getProperties() {
    return getPropertyDataMap().values();
  }

  public String nextPropertyID() {
    if (getPropertyDataMap().isEmpty()) return "P0";

    int maxID = -1;
    for (PropertyData propertyData : getPropertyDataMap().values()) {
      try {
        String totalID = propertyData.getTotalID();
        // Assuming totalID format is always "TownID_P<number>"
        String[] parts = totalID.split("P");
        if (parts.length > 1) {
          int currentID = Integer.parseInt(parts[1]);
          if (currentID > maxID) {
            maxID = currentID;
          }
        }
      } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
        // Log the error or handle it as appropriate for your application
        // For now, we'll just skip this property if its ID is malformed
        System.err.println(
            "Warning: Malformed property ID encountered: " + propertyData.getTotalID());
      }
    }
    return "P" + (maxID + 1);
  }

  private PropertyData createAndStoreProperty(Vector3D p1, Vector3D p2, Object owner) {
    String propertyID = nextPropertyID();
    String id = this.getID() + "_" + propertyID;
    PropertyData newProperty;
    if (owner instanceof TerritoryData) {
      newProperty = new PropertyData(id, p1, p2, (TerritoryData) owner);
    } else if (owner instanceof ITanPlayer) {
      newProperty = new PropertyData(id, p1, p2, (ITanPlayer) owner);
    } else {
      throw new IllegalArgumentException("Unsupported owner type");
    }
    this.propertyDataMap.put(propertyID, newProperty);
    return newProperty;
  }

  public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, TerritoryData owner) {
    return createAndStoreProperty(p1, p2, owner);
  }

  public PropertyData registerNewProperty(Vector3D p1, Vector3D p2, ITanPlayer owner) {
    PropertyData newProperty = createAndStoreProperty(p1, p2, owner);
    owner.addProperty(newProperty);
    return newProperty;
  }

  public PropertyData getProperty(String id) {
    return getPropertyDataMap().get(id);
  }

  public PropertyData getProperty(Location location) {
    for (PropertyData propertyData : getProperties()) {
      if (propertyData.containsLocation(location)) {
        return propertyData;
      }
    }
    return null;
  }

  public void removeProperty(PropertyData propertyData) {
    this.propertyDataMap.remove(propertyData.getPropertyID());
  }

  public String getTownTag() {
    if (this.townTag == null)
      setTownTag(name.substring(0, Constants.getPrefixSize()).toUpperCase());
    return this.townTag;
  }

  public void setTownTag(String townTag) {
    this.townTag = townTag;
    applyToAllOnlinePlayer(PrefixUtil::updatePrefix);
  }

  public String getColoredTag() {
    return getChunkColor() + "[" + getTownTag() + "]";
  }

  public void kickPlayer(OfflinePlayer kickedPlayer) {
    ITanPlayer kickedITanPlayer = PlayerDataStorage.getInstance().getSync(kickedPlayer);
    removePlayer(kickedITanPlayer);

    broadcastMessageWithSound(
        Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS.get(kickedPlayer.getName()), SoundEnum.BAD);
    org.leralix.tan.utils.FoliaScheduler.runTask(
        org.leralix.tan.TownsAndNations.getPlugin(),
        () -> {
          Player player = kickedPlayer.getPlayer();
          if (player != null && player.isOnline()) {
            TanChatUtils.message(
                player, Lang.GUI_TOWN_MEMBER_KICKED_SUCCESS_PLAYER.get(player), SoundEnum.BAD);
          }
        });
  }

  public boolean haveNoLeader() {
    return this.uuidLeader == null;
  }

  public void removeAllLandmark() {
    for (Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(this)) {
      landmark.removeOwnership();
    }
  }

  @Override
  public synchronized void delete() {
    super.delete();

    if (haveOverlord()) {
      RegionData regionData = RegionDataStorage.getInstance().getSync(this.overlordID);
      if (regionData != null) {
        regionData.removeVassal(this);
      }
    }

    removeAllLandmark(); // Remove all Landmark from the deleted town
    removeAllProperty(); // Remove all Property from the deleted town

    List<String> playersToRemove = new ArrayList<>(getPlayerIDList());
    for (String playerID : playersToRemove) {
      removePlayer(playerID);
    }

    TeamUtils.updateAllScoreboardColor();
    TownDataStorage.getInstance().deleteTown(this);
  }

  private void removeAllProperty() {
    Iterator<PropertyData> iterator = getProperties().iterator();
    while (iterator.hasNext()) {
      PropertyData propertyData = iterator.next();
      propertyData.delete();
      iterator.remove();
    }
  }

  @Override
  public void openMainMenu(Player player) {
    PlayerGUI.dispatchPlayerTown(player);
  }

  @Override
  public boolean canHaveVassals() {
    return false;
  }

  @Override
  public boolean canHaveOverlord() {
    return true;
  }

  @Override
  public List<String> getVassalsID() {
    return Collections.emptyList();
  }

  @Override
  public boolean isVassal(String territoryID) {
    return false;
  }
}
