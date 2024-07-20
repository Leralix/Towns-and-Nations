package org.tan.TownsAndNations.DataClass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.DataClass.territoryData.ITerritoryData;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.enums.SoundEnum;
import org.tan.TownsAndNations.utils.ConfigUtil;
import org.tan.TownsAndNations.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class AttackData {

    private final String ID;
    private String name;
    private final String mainDefenderID;
    private final String mainAttackerID;
    private Collection<String> defendersID;
    private Collection<String> attackersID;

    private final long startTime;
    private final long endTime;

    public AttackData(String ID, String name, ITerritoryData mainDefender, ITerritoryData mainAttacker, long startTime){
        this.ID = ID;
        this.name = name;
        this.mainAttackerID = mainAttacker.getID();
        this.mainDefenderID = mainDefender.getID();
        this.defendersID = new ArrayList<>();
        this.attackersID = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = this.startTime + ConfigUtil.getCustomConfig("config.yml").getLong("WarDurationTime") * 60000;

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

    public Collection<ITerritoryData> getDefenders() {
        Collection<ITerritoryData> defenders = new ArrayList<>();
        for(String defenderID : defendersID){
            defenders.add(TerritoryUtil.getTerritory(defenderID));
        }
        return defenders;
    }

    public Collection<ITerritoryData> getAttackers() {
        Collection<ITerritoryData> attackers = new ArrayList<>();
        for(String defenderID : defendersID){
            attackers.add(TerritoryUtil.getTerritory(defenderID));
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
        Collection<ITerritoryData> territoryData = getAttackers();
        territoryData.addAll(getDefenders());
        for(ITerritoryData territory : territoryData){
            territory.broadCastMessageWithSound(message, soundEnum);
        }
    }

    public void setUpStartOfAttack(){
        long currentTime = new Date().getTime();
        long timeLeftBeforeStart = startTime - currentTime;
        long timeLeftBeforeWarning = startTime - currentTime + 60000; //Warning 1 minute before

        BukkitRunnable startOfWar = new BukkitRunnable() {
            @Override
            public void run() {
                startWar();
            }
        };
        startOfWar.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeStart);

        BukkitRunnable warningStartOfWar = new BukkitRunnable() {
            @Override
            public void run() {broadCastMessageWithSound("War begin in 1 minute", SoundEnum.WAR);
            }
        };
        warningStartOfWar.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeWarning);
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
            itemMeta.setDisplayName(name);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


}
