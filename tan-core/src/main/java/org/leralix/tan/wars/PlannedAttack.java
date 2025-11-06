package org.leralix.tan.wars;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.time.Instant;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DefenderAcceptDemandsBeforeWarInternalEvent;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.wars.capture.CaptureManager;
import org.leralix.tan.wars.legacy.CreateAttackData;
import org.leralix.tan.wars.legacy.CurrentAttack;
import org.leralix.tan.wars.legacy.WarRole;

public class PlannedAttack {

  private final String ID;
  private String name;
  private final Collection<String> defendersID;
  private final Collection<String> attackersID;

  /** The start time of the war, in milliseconds since January 1, 1970 */
  final long startTime;

  /** The end time of the war, in milliseconds since January 1, 1970 */
  private final long endTime;

  private final War war;
  private final WarRole warRole;

  private transient ScheduledTask warStartTask;
  private transient ScheduledTask warWarningTask;

  boolean isAdminApproved;

  /**
   * Constructor for a Planned attack
   *
   * @param id The ID of the attack
   * @param createAttackData Data related to the attack and its war.
   */
  public PlannedAttack(String id, CreateAttackData createAttackData) {
    this.ID = id;

    this.war = createAttackData.getWar();
    this.warRole = createAttackData.getAttackingSide();

    this.name =
        Lang.BASIC_ATTACK_NAME.get(
            Lang.getServerLang(), war.getMainAttacker().getName(), war.getMainDefender().getName());

    this.isAdminApproved =
        !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AdminApproval", false);

    this.attackersID = new ArrayList<>();
    this.attackersID.add(war.getMainAttackerID());
    this.defendersID = new ArrayList<>();
    this.defendersID.add(war.getMainDefenderID());

    this.startTime = new Date().getTime() + (long) createAttackData.getSelectedTime() * 60 * 1000;
    this.endTime = this.startTime + Constants.getAttackDuration() * 60 * 1000;

    war.getMainDefender().addPlannedAttack(this);
    war.getMainAttacker().addPlannedAttack(this);

    setUpStartOfAttack();
  }

  public String getID() {
    return ID;
  }

  public String getName() {
    return name;
  }

  public War getWar() {
    return war;
  }

  public boolean isAdminApproved() {
    return isAdminApproved;
  }

  public void setAdminApproved(boolean isAdminApproved) {
    this.isAdminApproved = isAdminApproved;
  }

  public Collection<ITanPlayer> getDefendingPlayers() {
    Collection<ITanPlayer> defenders = new ArrayList<>();
    for (TerritoryData defendingTerritory : getDefendingTerritories()) {
      defenders.addAll(defendingTerritory.getITanPlayerList());
    }
    return defenders;
  }

  public Collection<ITanPlayer> getAttackersPlayers() {
    Collection<ITanPlayer> defenders = new ArrayList<>();
    for (TerritoryData attackingTerritory : getAttackingTerritories()) {
      defenders.addAll(attackingTerritory.getITanPlayerList());
    }
    return defenders;
  }

  public Collection<TerritoryData> getDefendingTerritories() {
    Collection<TerritoryData> defenders = new ArrayList<>();
    for (String defenderID : defendersID) {
      defenders.add(TerritoryUtil.getTerritory(defenderID));
    }
    return defenders;
  }

  public Collection<TerritoryData> getAttackingTerritories() {
    Collection<TerritoryData> attackers = new ArrayList<>();
    for (String attackerID : attackersID) {
      attackers.add(TerritoryUtil.getTerritory(attackerID));
    }
    return attackers;
  }

  public void broadCastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
    Collection<TerritoryData> territoryData = getAttackingTerritories();
    territoryData.addAll(getDefendingTerritories());
    for (TerritoryData territory : territoryData) {
      territory.broadcastMessageWithSound(message, soundEnum);
    }
  }

  public void setUpStartOfAttack() {

    // Convesion to ticks
    long currentTime = new Date().getTime();
    long timeLeftBeforeStart = (long) ((startTime - currentTime) * 0.02);
    long timeLeftBeforeWarning = timeLeftBeforeStart - 1200; // Warning 1 minute before

    if (timeLeftBeforeStart <= 0) {
      startWar(startTime - timeLeftBeforeStart);
      return;
    }

    warStartTask =
        org.leralix.tan.utils.FoliaScheduler.runTaskLater(
            TownsAndNations.getPlugin(), () -> startWar(startTime), timeLeftBeforeStart);

    if (timeLeftBeforeWarning > 0) {
      warWarningTask =
          org.leralix.tan.utils.FoliaScheduler.runTaskLater(
              TownsAndNations.getPlugin(),
              () ->
                  broadCastMessageWithSound(
                      Lang.ATTACK_START_IN_1_MINUTES.get(name), SoundEnum.WAR),
              timeLeftBeforeWarning);
    }
  }

  void startWar(long startTime) {
    broadCastMessageWithSound(Lang.ATTACK_START_NOW.get(name), SoundEnum.WAR);
    CurrentAttacksStorage.startAttack(this, startTime, endTime);
  }

  public void addDefender(TerritoryData territory) {
    defendersID.add(territory.getID());
  }

  public void addAttacker(TerritoryData territoryData) {
    attackersID.add(territoryData.getID());
  }

  public ItemStack getAdminIcon(LangType langType) {

    long startDate = startTime - new Date().getTime() / 50;
    long attackDuration = endTime - startTime;

    ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(itemMeta, "§a" + name);
      ArrayList<String> lore = new ArrayList<>();
      lore.add(Lang.ATTACK_ICON_DESC_1.get(langType, war.getMainAttacker().getName()));
      lore.add(Lang.ATTACK_ICON_DESC_2.get(langType, war.getMainDefender().getName()));
      lore.add(Lang.ATTACK_ICON_DESC_3.get(langType, Integer.toString(getNumberOfAttackers())));
      lore.add(Lang.ATTACK_ICON_DESC_4.get(langType, Integer.toString(getNumberOfDefenders())));
      lore.add(Lang.ATTACK_ICON_DESC_6.get(langType, DateUtil.getDateStringFromTicks(startDate)));
      lore.add(
          Lang.ATTACK_ICON_DESC_7.get(langType, DateUtil.getDateStringFromTicks(attackDuration)));
      if (isAdminApproved) {
        lore.add(Lang.ATTACK_ICON_DESC_ADMIN_APPROVED.get(langType));
      } else {
        lore.add(Lang.ATTACK_ICON_DESC_ADMIN_NOT_APPROVED.get(langType));
        lore.add(Lang.LEFT_CLICK_TO_AUTHORIZE.get(langType));
        lore.add(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get(langType));
        lore.add(Lang.ATTACK_WILL_NOT_TRIGGER_IF_NOT_APPROVED.get(langType));
      }
      org.leralix.tan.utils.text.ComponentUtil.setLore(itemMeta, lore);
    }
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public ItemStack getIcon(ITanPlayer tanPlayer, TerritoryData territoryConcerned) {

    long startDate = startTime - new Date().getTime() / 50;
    long attackDuration = endTime - startTime;
    FilledLang exactTimeStart =
        TimeZoneManager.getInstance()
            .formatDateForPlayer(tanPlayer, Instant.ofEpochSecond(startTime / 20));
    LangType langType = tanPlayer.getLang();

    ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta != null) {
      org.leralix.tan.utils.text.ComponentUtil.setDisplayName(itemMeta, "§a" + name);
      ArrayList<String> lore = new ArrayList<>();
      lore.add(Lang.ATTACK_ICON_DESC_1.get(langType, war.getMainAttacker().getName()));
      lore.add(Lang.ATTACK_ICON_DESC_2.get(langType, war.getMainDefender().getName()));
      lore.add(Lang.ATTACK_ICON_DESC_3.get(langType, Integer.toString(getNumberOfAttackers())));
      lore.add(Lang.ATTACK_ICON_DESC_4.get(langType, Integer.toString(getNumberOfDefenders())));
      lore.add(
          Lang.ATTACK_ICON_DESC_6.get(
              langType, DateUtil.getDateStringFromTicks(startDate), exactTimeStart.get(langType)));
      lore.add(
          Lang.ATTACK_ICON_DESC_7.get(langType, DateUtil.getDateStringFromTicks(attackDuration)));
      lore.add(
          Lang.ATTACK_ICON_DESC_8.get(
              langType, getTerritoryRole(territoryConcerned).getName(langType)));

      org.leralix.tan.utils.text.ComponentUtil.setLore(itemMeta, lore);
    }
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  private int getNumberOfAttackers() {
    return attackersID.size();
  }

  private int getNumberOfDefenders() {
    return defendersID.size();
  }

  public void end() {
    if (warStartTask != null) {
      warStartTask.cancel();
    }
    if (warWarningTask != null) {
      warWarningTask.cancel();
    }

    // All chunks captured due to the war are now released
    CaptureManager.getInstance().removeCapture(this);

    CurrentAttack currentAttack = CurrentAttacksStorage.get(ID);
    if (currentAttack != null) {
      currentAttack.end();
    }
    for (TerritoryData territory : getAttackingTerritories()) {
      territory.removePlannedAttack(this);
    }
    for (TerritoryData territory : getDefendingTerritories()) {
      territory.removePlannedAttack(this);
    }
    PlannedAttackStorage.getInstance().delete(this);
  }

  private boolean isSecondaryAttacker(TerritoryData territoryConcerned) {
    return attackersID.contains(territoryConcerned.getID());
  }

  private boolean isSecondaryDefender(TerritoryData territoryConcerned) {
    return defendersID.contains(territoryConcerned.getID());
  }

  public WarRole getRole(ITanPlayer player) {
    List<TerritoryData> territories = player.getAllTerritoriesPlayerIsInSync();
    if (territories == null) return WarRole.NEUTRAL;
    for (TerritoryData territoryData : territories) {
      WarRole role = getTerritoryRole(territoryData);
      if (role != WarRole.NEUTRAL) {
        return role;
      }
    }
    return WarRole.NEUTRAL;
  }

  public WarRole getTerritoryRole(TerritoryData territory) {
    if (war.isMainAttacker(territory)) return WarRole.MAIN_ATTACKER;
    if (war.isMainDefender(territory)) return WarRole.MAIN_DEFENDER;
    if (isSecondaryAttacker(territory)) return WarRole.OTHER_ATTACKER;
    if (isSecondaryDefender(territory)) return WarRole.OTHER_DEFENDER;
    return WarRole.NEUTRAL;
  }

  public void removeBelligerent(TerritoryData territory) {
    String territoryID = territory.getID();
    // no need to check, it only removes if it is a part of it
    attackersID.remove(territoryID);
    defendersID.remove(territoryID);
  }

  public void territorySurrendered() {
    EventManager.getInstance()
        .callEvent(
            new DefenderAcceptDemandsBeforeWarInternalEvent(
                war.getMainDefender(), war.getMainAttacker()));
    war.territorySurrender(warRole);
    end();
  }

  public ItemStack getAttackingIcon(LangType langType) {
    ItemStack itemStack = new ItemStack(Material.IRON_HELMET);
    ItemMeta itemMeta = itemStack.getItemMeta();
    List<String> lore = new ArrayList<>();
    org.leralix.tan.utils.text.ComponentUtil.setDisplayName(
        itemMeta, Lang.GUI_ATTACKING_SIDE_ICON.get(langType));
    lore.add(Lang.GUI_ATTACKING_SIDE_ICON_DESC1.get(langType));
    for (TerritoryData territoryData : getAttackingTerritories()) {
      lore.add(Lang.GUI_ICON_LIST.get(langType, territoryData.getBaseColoredName()));
    }
    org.leralix.tan.utils.text.ComponentUtil.setLore(itemMeta, lore);
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public ItemStack getDefendingIcon(LangType langType) {
    ItemStack itemStack = new ItemStack(Material.DIAMOND_HELMET);
    ItemMeta itemMeta = itemStack.getItemMeta();
    List<String> lore = new ArrayList<>();
    org.leralix.tan.utils.text.ComponentUtil.setDisplayName(
        itemMeta, Lang.GUI_DEFENDING_SIDE_ICON.get(langType));
    lore.add(Lang.GUI_DEFENDING_SIDE_ICON_DESC1.get(langType));
    for (TerritoryData territoryData : getDefendingTerritories()) {
      lore.add(Lang.GUI_ICON_LIST.get(langType, territoryData.getBaseColoredName()));
    }
    org.leralix.tan.utils.text.ComponentUtil.setLore(itemMeta, lore);
    itemStack.setItemMeta(itemMeta);
    return itemStack;
  }

  public void rename(String message) {
    this.name = message;
  }

  public List<OfflinePlayer> getAllOfflinePlayers() {
    List<ITanPlayer> res = new ArrayList<>(getDefendingPlayers());
    res.addAll(getAttackersPlayers());
    return res.stream().map(ITanPlayer::getOfflinePlayer).filter(Objects::nonNull).toList();
  }

  public List<Player> getAllOnlinePlayers() {
    return getAllOfflinePlayers().stream()
        .map(OfflinePlayer::getPlayer)
        .filter(Objects::nonNull)
        .toList();
  }
}
