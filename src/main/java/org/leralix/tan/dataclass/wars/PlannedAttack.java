package org.leralix.tan.dataclass.wars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.leralix.tan.dataclass.wars.wargoals.WarGoal;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.DefenderAcceptDemandsBeforeWarInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.DateUtil;
import org.leralix.tan.utils.TerritoryUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class PlannedAttack {

    private final String ID;
    private String name;
    private final String mainDefenderID;
    private final String mainAttackerID;
    private final Collection<String> defendersID;
    private final Collection<String> attackersID;

    private final long startTime;
    private final long endTime;
    private final WarGoal warGoal;

    boolean isAdminApproved;

    public PlannedAttack(String id, CreateAttackData createAttackData, long startTime){
        this.ID = id;
        this.name = Lang.BASIC_ATTACK_NAME.get(createAttackData.getMainAttacker().getName(), createAttackData.getMainDefender().getName());
        this.mainAttackerID = createAttackData.getMainAttacker().getID();
        this.mainDefenderID = createAttackData.getMainDefender().getID();

        this.isAdminApproved = !ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("AdminApproval",false);

        this.attackersID = new ArrayList<>();
        this.attackersID.add(mainAttackerID);
        this.defendersID = new ArrayList<>();
        this.defendersID.add(mainDefenderID);

        this.startTime = (long) (new Date().getTime() * 0.02 + startTime);
        this.endTime = this.startTime + ConfigUtil.getCustomConfig(ConfigTag.MAIN).getLong("WarDuration") * 1200;

        createAttackData.getMainDefender().addPlannedAttack(this);
        createAttackData.getMainAttacker().addPlannedAttack(this);

        this.warGoal = createAttackData.getWargoal();

        setUpStartOfAttack();
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public TerritoryData getMainDefender() {
        return TerritoryUtil.getTerritory(mainDefenderID);
    }

    public TerritoryData getMainAttacker() {
        return TerritoryUtil.getTerritory(mainAttackerID);
    }

    public boolean isAdminApproved() {
        return isAdminApproved;
    }

    public void setAdminApproved(boolean isAdminApproved) {
        this.isAdminApproved = isAdminApproved;
    }

    public Collection<ITanPlayer> getDefendingPlayers() {
        Collection<ITanPlayer> defenders = new ArrayList<>();
        for(TerritoryData defendingTerritory : getDefendingTerritories()){
            defenders.addAll(defendingTerritory.getITanPlayerList());
        }
        return defenders;
    }

    public Collection<ITanPlayer> getAttackersPlayers() {
        Collection<ITanPlayer> defenders = new ArrayList<>();
        for(TerritoryData attackingTerritory : getAttackingTerritories()){
            defenders.addAll(attackingTerritory.getITanPlayerList());
        }
        return defenders;
    }

    public Collection<TerritoryData> getDefendingTerritories() {
        Collection<TerritoryData> defenders = new ArrayList<>();
        for(String defenderID : defendersID){
            defenders.add(TerritoryUtil.getTerritory(defenderID));
        }
        return defenders;
    }

    public Collection<TerritoryData> getAttackingTerritories() {
        Collection<TerritoryData> attackers = new ArrayList<>();
        for(String attackerID : attackersID){
            attackers.add(TerritoryUtil.getTerritory(attackerID));
        }
        return attackers;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void broadCastMessageWithSound(String message, SoundEnum soundEnum){
        Collection<TerritoryData> territoryData = getAttackingTerritories();
        territoryData.addAll(getDefendingTerritories());
        for(TerritoryData territory : territoryData){
            territory.broadcastMessageWithSound(message, soundEnum);
        }
    }

    public void setUpStartOfAttack(){
        long timeLeftBeforeStart = (long) (startTime - new Date().getTime() * 0.02);
        long timeLeftBeforeWarning = timeLeftBeforeStart - 1200; //Warning 1 minute before
        BukkitRunnable startOfWar = new BukkitRunnable() {
            @Override
            public void run() {
                startWar();
            }
        };
        startOfWar.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeStart);


        BukkitRunnable warningStartOfWar = new BukkitRunnable() {
            @Override
            public void run() {
                broadCastMessageWithSound("War begin in 1 minute", SoundEnum.WAR);
            }
        };
        warningStartOfWar.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeWarning);
    }

    private void startWar() {
        broadCastMessageWithSound("War start", SoundEnum.WAR);
        CurrentAttacksStorage.startAttack(this);
        remove();
    }

    public void addDefender(TerritoryData territory){
        defendersID.add(territory.getID());
    }
    public void addAttacker(TerritoryData territoryData){
        attackersID.add(territoryData.getID());
    }


    public ItemStack getAdminIcon(){

        long startDate = getStartTime() - new Date().getTime() / 50;
        long attackDuration = getEndTime() - getStartTime();

        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName(ChatColor.GREEN + name);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_3.get(getNumberOfAttackers()));
            lore.add(Lang.ATTACK_ICON_DESC_4.get(getNumberOfDefenders()));
            lore.add(Lang.ATTACK_ICON_DESC_5.get(warGoal.getCurrentDesc()));
            lore.add(Lang.ATTACK_ICON_DESC_6.get(DateUtil.getDateStringFromTicks(startDate)));
            lore.add(Lang.ATTACK_ICON_DESC_7.get(DateUtil.getDateStringFromTicks(attackDuration)));
            if(isAdminApproved){
                lore.add(Lang.ATTACK_ICON_DESC_ADMIN_APPROVED.get());
            }else{
                lore.add(Lang.ATTACK_ICON_DESC_ADMIN_NOT_APPROVED.get());
                lore.add(Lang.LEFT_CLICK_TO_AUTHORIZE.get());
                lore.add(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get());
                lore.add(Lang.ATTACK_WILL_NOT_TRIGGER_IF_NOT_APPROVED.get());
            }
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getIcon(ITanPlayer tanPlayer, TerritoryData territoryConcerned){

        long startDate = getStartTime() - new Date().getTime() / 50;
        long attackDuration = getEndTime() - getStartTime();
        String exactTimeStart = TimeZoneManager.getInstance().formatDateForPlayer(tanPlayer, Instant.ofEpochSecond(getStartTime() / 20));

        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName(ChatColor.GREEN + name);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_3.get(getNumberOfAttackers()));
            lore.add(Lang.ATTACK_ICON_DESC_4.get(getNumberOfDefenders()));
            lore.add(Lang.ATTACK_ICON_DESC_5.get(warGoal.getCurrentDesc()));
            lore.add(Lang.ATTACK_ICON_DESC_6.get(DateUtil.getDateStringFromTicks(startDate), exactTimeStart));
            lore.add(Lang.ATTACK_ICON_DESC_7.get(DateUtil.getDateStringFromTicks(attackDuration)));
            lore.add(Lang.ATTACK_ICON_DESC_8.get(getTerritoryRole(territoryConcerned).getName()));

            lore.add(Lang.GUI_GENERIC_CLICK_TO_OPEN.get());
            itemMeta.setLore(lore);
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


    public void remove() {
        for(TerritoryData territory : getAttackingTerritories()){
            territory.removePlannedAttack(this);
        }
        for(TerritoryData territory : getDefendingTerritories()){
            territory.removePlannedAttack(this);
        }
        PlannedAttackStorage.remove(this);
    }

    public boolean isMainAttacker(TerritoryData territory) {
        return territory.getID().equals(mainAttackerID);
    }

    public boolean isMainDefender(TerritoryData territory) {
        return territory.getID().equals(mainDefenderID);
    }

    private boolean isSecondaryAttacker(TerritoryData territoryConcerned) {
        return attackersID.contains(territoryConcerned.getID());
    }

    private boolean isSecondaryDefender(TerritoryData territoryConcerned) {
        return defendersID.contains(territoryConcerned.getID());
    }

    public WarGoal getWarGoal() {
        return warGoal;
    }

    public WarRole getRole(ITanPlayer player){
        for(TerritoryData territoryData : player.getAllTerritoriesPlayerIsIn()){
            WarRole role = getTerritoryRole(territoryData);
            if(role != WarRole.NEUTRAL){
                return role;
            }
        }
        return WarRole.NEUTRAL;
    }

    public WarRole getTerritoryRole(TerritoryData territory) {
        if(isMainAttacker(territory))
            return WarRole.MAIN_ATTACKER;
        if(isMainDefender(territory))
            return WarRole.MAIN_DEFENDER;
        if(isSecondaryAttacker(territory))
            return WarRole.OTHER_ATTACKER;
        if(isSecondaryDefender(territory))
            return WarRole.OTHER_DEFENDER;
        return WarRole.NEUTRAL;
    }

    public void removeBelligerent(TerritoryData territory) {
        String territoryID = territory.getID();
        //no need to check, it only removes if it is a part of it
        attackersID.remove(territoryID);
        defendersID.remove(territoryID);
    }

    public void defenderSurrendered() {

        EventManager.getInstance().callEvent(new DefenderAcceptDemandsBeforeWarInternalEvent(getMainDefender(), getMainAttacker()));

        getWarGoal().applyWarGoal();
        remove();
    }

    public ItemStack getAttackingIcon() {
        ItemStack itemStack = new ItemStack(Material.IRON_HELMET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(Lang.GUI_ATTACKING_SIDE_ICON.get());
        lore.add(Lang.GUI_ATTACKING_SIDE_ICON_DESC1.get());
        for(TerritoryData territoryData : getAttackingTerritories()){
            lore.add(Lang.GUI_ICON_LIST.get(territoryData.getBaseColoredName()));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getDefendingIcon() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(Lang.GUI_DEFENDING_SIDE_ICON.get());
        lore.add(Lang.GUI_DEFENDING_SIDE_ICON_DESC1.get());
        for(TerritoryData territoryData : getDefendingTerritories()){
            lore.add(Lang.GUI_ICON_LIST.get(territoryData.getBaseColoredName()));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void rename(String message) {
        this.name = message;
    }

    public List<Player> getAllPlayers() {
        List<ITanPlayer> res = new ArrayList<>(getDefendingPlayers());
        res.addAll(getAttackersPlayers());
        return res.stream().map(ITanPlayer::getPlayer).toList();
    }
}
