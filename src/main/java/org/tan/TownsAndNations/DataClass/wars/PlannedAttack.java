package org.tan.TownsAndNations.DataClass.wars;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.DataClass.wars.wargoals.WarGoal;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.storage.CurrentAttacksStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlannedAttackStorage;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.DateUtil;
import org.tan.TownsAndNations.utils.TerritoryUtil;

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
    WarGoal warGoal;

    public PlannedAttack(String ID, String name, CreateAttackData createAttackData, long startTime){
        this.ID = ID;
        this.name = name;
        this.mainAttackerID = createAttackData.getMainAttacker().getID();
        this.mainDefenderID = createAttackData.getMainDefender().getID();

        this.attackersID = new ArrayList<>();
        this.attackersID.add(mainAttackerID);
        this.defendersID = new ArrayList<>();
        this.defendersID.add(mainDefenderID);

        this.startTime = (long) (new Date().getTime() * 0.02 + startTime);
        this.endTime = this.startTime + ConfigUtil.getCustomConfig("config.yml").getLong("WarDurationTime") * 1200;

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

    public ITerritoryData getMainDefender() {
        return TerritoryUtil.getTerritory(mainDefenderID);
    }

    public ITerritoryData getMainAttacker() {
        return TerritoryUtil.getTerritory(mainAttackerID);
    }

    public Collection<PlayerData> getDefendingPlayers() {
        Collection<PlayerData> defenders = new ArrayList<>();
        for(ITerritoryData defendingTerritory : getDefendingTerritory()){
            defenders.addAll(defendingTerritory.getPlayerDataList());
        }
        return defenders;
    }

    public Collection<PlayerData> getAttackersPlayers() {
        Collection<PlayerData> defenders = new ArrayList<>();
        for(ITerritoryData attackingTerritory : getAttackingTerritory()){
            defenders.addAll(attackingTerritory.getPlayerDataList());
        }
        return defenders;
    }

    public Collection<ITerritoryData> getDefendingTerritory() {
        Collection<ITerritoryData> defenders = new ArrayList<>();
        for(String defenderID : defendersID){
            defenders.add(TerritoryUtil.getTerritory(defenderID));
        }
        return defenders;
    }

    public Collection<ITerritoryData> getAttackingTerritory() {
        Collection<ITerritoryData> attackers = new ArrayList<>();
        for(String attackerID : attackersID){
            attackers.add(TerritoryUtil.getTerritory(attackerID));
        }
        return attackers;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void broadCastMessageWithSound(String message, SoundEnum soundEnum){
        Collection<ITerritoryData> territoryData = getAttackingTerritory();
        territoryData.addAll(getDefendingTerritory());
        for(ITerritoryData territory : territoryData){
            territory.broadCastMessageWithSound(message, soundEnum);
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

    public void addDefender(ITerritoryData territory){
        defendersID.add(territory.getID());
    }
    public void addAttacker(ITerritoryData territoryData){
        attackersID.add(territoryData.getID());
    }

    public ItemStack getIcon(ITerritoryData territoryConcerned){
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName(name);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_3.get(getNumberOfAttackers()));
            lore.add(Lang.ATTACK_ICON_DESC_4.get(getNumberOfDefenders()));
            lore.add(Lang.ATTACK_ICON_DESC_5.get(warGoal.getCurrentDesc()));
            lore.add(Lang.ATTACK_ICON_DESC_6.get(DateUtil.getStringDeltaDateTime((long) (getStartTime() - new Date().getTime() * 0.02))));
            lore.add(Lang.ATTACK_ICON_DESC_7.get(DateUtil.getStringDeltaDateTime((long) ((getEndTime() - getStartTime())))));
            lore.add(Lang.ATTACK_ICON_DESC_8.get(getTerritoryRole(territoryConcerned).getName()));

            lore.add(Lang.GUI_LEFT_CLICK_TO_INTERACT.get());
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
        for(ITerritoryData territory : getAttackingTerritory()){
            territory.removePlannedAttack(this);
        }
        for(ITerritoryData territory : getDefendingTerritory()){
            territory.removePlannedAttack(this);
        }
        PlannedAttackStorage.remove(this);
    }

    public boolean isMainAttacker(ITerritoryData territory) {
        return territory.getID().equals(mainAttackerID);
    }

    public boolean isMainDefender(ITerritoryData territory) {
        return territory.getID().equals(mainDefenderID);
    }

    private boolean isSecondaryAttacker(ITerritoryData territoryConcerned) {
        return attackersID.contains(territoryConcerned.getID());
    }

    private boolean isSecondaryDefender(ITerritoryData territoryConcerned) {
        return defendersID.contains(territoryConcerned.getID());
    }

    public WarGoal getWarGoal() {
        return warGoal;
    }

    public WarRole getTerritoryRole(ITerritoryData territory) {
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

    public void removeBelligerent(ITerritoryData territory) {
        String territoryID = territory.getID();
        //no need to check, it only removes if it is a part of it
        attackersID.remove(territoryID);
        defendersID.remove(territoryID);
    }

    public void defenderSurrendered() {
        broadCastMessageWithSound(Lang.DEFENSIVE_SIDE_HAS_SURRENDER.get(getMainDefender().getColoredName(), getMainAttacker().getColoredName()), SoundEnum.WAR);
        getWarGoal().applyWarGoal();
        remove();
    }

    public ItemStack getAttackingIcon() {
        ItemStack itemStack = new ItemStack(Material.IRON_HELMET);
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        itemMeta.setDisplayName(Lang.GUI_ATTACKING_SIDE_ICON.get());
        lore.add(Lang.GUI_ATTACKING_SIDE_ICON_DESC1.get());
        for(ITerritoryData territoryData : getAttackingTerritory()){
            lore.add(Lang.GUI_ICON_LIST.get(territoryData.getColoredName()));
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
        for(ITerritoryData territoryData : getAttackingTerritory()){
            lore.add(Lang.GUI_ICON_LIST.get(territoryData.getColoredName()));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
