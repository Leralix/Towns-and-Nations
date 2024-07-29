package org.tan.TownsAndNations.DataClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.DateUtil;
import org.tan.TownsAndNations.utils.SoundUtil;
import org.tan.TownsAndNations.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class AttackData {

    private final String ID;
    private String name;
    private final String mainDefenderID;
    private final String mainAttackerID;
    private final Collection<String> defendersID;
    private final Collection<String> attackersID;

    private final long startTime;
    private final long endTime;

    public AttackData(String ID, String name, ITerritoryData mainDefender, ITerritoryData mainAttacker, long startTime){
        this.ID = ID;
        this.name = name;
        this.mainAttackerID = mainAttacker.getID();
        this.mainDefenderID = mainDefender.getID();

        this.attackersID = new ArrayList<>();
        this.attackersID.add(mainAttackerID);
        this.defendersID = new ArrayList<>();
        this.defendersID.add(mainDefenderID);

        this.startTime = (long) (new Date().getTime() * 0.02 + startTime);
        this.endTime = this.startTime + ConfigUtil.getCustomConfig("config.yml").getLong("WarDurationTime") * 1200;

        mainDefender.addWar(this);
        mainAttacker.addWar(this);

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
        for(ITerritoryData attackingTerritory : getDefendingTerritory()){
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
        System.out.println("War warning in " + timeLeftBeforeWarning + "ticks");
        System.out.println("War start in " + timeLeftBeforeStart + "ticks");
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

        BukkitRunnable warningStartOfWar2 = new BukkitRunnable() {
            @Override
            public void run() {
                broadCastMessageWithSound("War begin in x minute", SoundEnum.WAR);
            }
        };
        warningStartOfWar2.runTaskLater(TownsAndNations.getPlugin(), startTime / 10);
    }

    private void startWar() {
        broadCastMessageWithSound("War start", SoundEnum.WAR);
    }

    public void AddDefender(ITerritoryData territory){
        defendersID.add(territory.getID());
    }
    public void AddAttacker(ITerritoryData territoryData){
        attackersID.add(territoryData.getID());
    }

    public ItemStack getIcon(){
        ItemStack itemStack = new ItemStack(Material.IRON_SWORD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName( name);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_3.get(DateUtil.getStringDeltaDateTime((long) (getStartTime() - new Date().getTime() * 0.02))));
            lore.add(Lang.ATTACK_ICON_DESC_4.get(DateUtil.getStringDeltaDateTime((long) ((getEndTime() - getStartTime())))));
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public void remove() {
        for(ITerritoryData territory : getAttackingTerritory()){
            territory.removeWar(this);
        }
        for(ITerritoryData territory : getDefendingTerritory()){
            territory.removeWar(this);
        }
    }

    public boolean isMainAttacker(ITerritoryData territory) {
        return territory.getID().equals(mainAttackerID);
    }
}
