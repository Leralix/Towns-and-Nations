package org.leralix.tan.war;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DefenderAcceptDemandsBeforeWarInternalEvent;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.DateUtil;
import org.leralix.tan.war.capture.CaptureManager;
import org.leralix.tan.war.info.AttackNotYetStarted;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.AttackResultCancelled;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.WarRole;

import java.time.Instant;
import java.util.*;

public class PlannedAttack {

    private final String ID;
    private String name;
    private final Collection<String> defendersID;
    private final Collection<String> attackersID;
    private AttackResult attackResult;

    /**
     * The start time of the war, in milliseconds since January 1, 1970
     */
    final long startTime;
    /**
     * The end time of the war, in milliseconds since January 1, 1970
     */
    private final long endTime;
    private final War war;
    private final WarRole warRole;

    private transient BukkitRunnable warStartTask;
    private transient BukkitRunnable warWarningTask;

    boolean isAdminApproved;

    /**
     * Constructor for a Planned attack
     *
     * @param id               The ID of the attack
     * @param createAttackData Data related to the attack and its war.
     */
    public PlannedAttack(String id, CreateAttackData createAttackData) {
        this.ID = id;

        this.war = createAttackData.getWar();
        this.warRole = createAttackData.getAttackingSide();

        this.name = Lang.BASIC_ATTACK_NAME.get(
                Lang.getServerLang(),
                war.getMainAttacker().getName(),
                war.getMainDefender().getName()
        );

        this.isAdminApproved = !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AdminApproval", false);

        this.attackersID = new ArrayList<>();
        this.attackersID.add(war.getMainAttackerID());
        this.defendersID = new ArrayList<>();
        this.defendersID.add(war.getMainDefenderID());

        this.startTime = new Date().getTime() + (long) createAttackData.getSelectedTime() * 60 * 1000;
        this.endTime = this.startTime + Constants.getAttackDuration() * 60 * 1000;

        this.attackResult = new AttackNotYetStarted();

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

        //Convesion to ticks
        long currentTime = new Date().getTime();
        long timeLeftBeforeStart = (long) ((startTime - currentTime) * 0.02);
        long timeLeftBeforeWarning = timeLeftBeforeStart - 1200; //Warning 1 minute before


        if (timeLeftBeforeStart <= 0) {
            startWar(startTime - timeLeftBeforeStart);
            return;
        }

        warStartTask = new BukkitRunnable() {
            @Override
            public void run() {
                startWar(startTime);
            }
        };
        warStartTask.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeStart);

        if (timeLeftBeforeWarning > 0) {
            warWarningTask = new BukkitRunnable() {
                @Override
                public void run() {
                    broadCastMessageWithSound(Lang.ATTACK_START_IN_1_MINUTES.get(name), SoundEnum.WAR);
                }
            };
            warWarningTask.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeWarning);
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

    public IconBuilder getAdminIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZoneEnum) {

        IconBuilder iconBuilder = getBaseIcon(iconManager, langType, timeZoneEnum);

        if(isAdminApproved) {
            return iconBuilder.addDescription(Lang.ATTACK_ICON_DESC_ADMIN_APPROVED.get());
        }
        else {
            return iconBuilder.addDescription(
                    Lang.ATTACK_ICON_DESC_ADMIN_NOT_APPROVED.get(),
                    Lang.LEFT_CLICK_TO_AUTHORIZE.get(),
                    Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get(),
                    Lang.ATTACK_WILL_NOT_TRIGGER_IF_NOT_APPROVED.get()
            );
        }
    }

    private IconBuilder getBaseIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZone) {
        long startDateInSeconds = (startTime - new Date().getTime()) / 1000;
        long attackDurationInSeconds = (endTime - startTime) / 1000;

        FilledLang exactTimeStart = TimeZoneManager.getInstance().formatDate(Instant.ofEpochMilli(startTime), timeZone, langType.getLocale());

        return iconManager.get(Material.IRON_SWORD)
                .setName(ChatColor.GREEN + name)
                .setDescription(
                        Lang.ATTACK_ICON_DESC_1.get(war.getMainAttacker().getName()),
                        Lang.ATTACK_ICON_DESC_2.get(war.getMainDefender().getName()),
                        Lang.ATTACK_ICON_DESC_3.get(Integer.toString(getNumberOfAttackers())),
                        Lang.ATTACK_ICON_DESC_4.get(Integer.toString(getNumberOfDefenders())),
                        Lang.ATTACK_ICON_DESC_6.get(DateUtil.getDateStringFromSeconds(startDateInSeconds), exactTimeStart.get(langType)),
                        Lang.ATTACK_ICON_DESC_7.get(DateUtil.getDateStringFromSeconds(attackDurationInSeconds))
                );
    }

    public IconBuilder getIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZone){
        IconBuilder mainIcon = getBaseIcon(iconManager, langType, timeZone);
        mainIcon.addDescription(attackResult.getResultLines());
        return mainIcon;
    }

    public IconBuilder getIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZone, TerritoryData territoryConcerned) {
        return getBaseIcon(iconManager, langType, timeZone)
                .addDescription(Lang.ATTACK_ICON_DESC_8.get(getTerritoryRole(territoryConcerned).getName(langType)));
    }

    private int getNumberOfAttackers() {
        return attackersID.size();
    }

    private int getNumberOfDefenders() {
        return defendersID.size();
    }

    public void end(AttackResult attackResult) {

        this.attackResult = attackResult;

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
    }


    private boolean isSecondaryAttacker(TerritoryData territoryConcerned) {
        return attackersID.contains(territoryConcerned.getID());
    }

    private boolean isSecondaryDefender(TerritoryData territoryConcerned) {
        return defendersID.contains(territoryConcerned.getID());
    }

    public WarRole getRole(ITanPlayer player) {
        for (TerritoryData territoryData : player.getAllTerritoriesPlayerIsIn()) {
            WarRole role = getTerritoryRole(territoryData);
            if (role != WarRole.NEUTRAL) {
                return role;
            }
        }
        return WarRole.NEUTRAL;
    }

    public WarRole getTerritoryRole(TerritoryData territory) {
        if (war.isMainAttacker(territory))
            return WarRole.MAIN_ATTACKER;
        if (war.isMainDefender(territory))
            return WarRole.MAIN_DEFENDER;
        if (isSecondaryAttacker(territory))
            return WarRole.OTHER_ATTACKER;
        if (isSecondaryDefender(territory))
            return WarRole.OTHER_DEFENDER;
        return WarRole.NEUTRAL;
    }

    public void removeBelligerent(TerritoryData territory) {
        String territoryID = territory.getID();
        //no need to check, it only removes if it is a part of it
        attackersID.remove(territoryID);
        defendersID.remove(territoryID);
    }

    public void territorySurrendered() {
        EventManager.getInstance().callEvent(new DefenderAcceptDemandsBeforeWarInternalEvent(war.getMainDefender(), war.getMainAttacker()));
        war.territorySurrender(warRole);
        end(new AttackResultCancelled());
    }

    public ItemStack getAttackingIcon(LangType langType) {
        ItemStack itemStack = new ItemStack(Material.IRON_HELMET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(Lang.GUI_ATTACKING_SIDE_ICON.get(langType));
        lore.add(Lang.GUI_ATTACKING_SIDE_ICON_DESC1.get(langType));
        for (TerritoryData territoryData : getAttackingTerritories()) {
            lore.add(Lang.GUI_ICON_LIST.get(langType, territoryData.getBaseColoredName()));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getDefendingIcon(LangType langType) {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(Lang.GUI_DEFENDING_SIDE_ICON.get(langType));
        lore.add(Lang.GUI_DEFENDING_SIDE_ICON_DESC1.get(langType));
        for (TerritoryData territoryData : getDefendingTerritories()) {
            lore.add(Lang.GUI_ICON_LIST.get(langType, territoryData.getBaseColoredName()));
        }
        itemMeta.setLore(lore);
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
        return getAllOfflinePlayers().stream().map(OfflinePlayer::getPlayer).filter(Objects::nonNull).toList();
    }

}
