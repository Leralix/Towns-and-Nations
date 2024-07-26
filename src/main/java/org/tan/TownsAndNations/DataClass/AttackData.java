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

        this.startTime = new Date().getTime() + startTime;
        this.endTime = this.startTime + ConfigUtil.getCustomConfig("config.yml").getLong("WarDurationTime") * 60000;

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
            public void run() {
                broadCastMessageWithSound("War begin in 1 minute", SoundEnum.WAR);
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
            itemMeta.setDisplayName( name);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Lang.ATTACK_ICON_DESC_1.get(getMainAttacker().getName()));
            lore.add(Lang.ATTACK_ICON_DESC_2.get(getMainDefender().getName()));
            int timeLeft = (int) ((getStartTime() - new Date().getTime()) / 60000);
            lore.add(Lang.ATTACK_ICON_DESC_3.get( timeLeft));
            int timeLeftEnd = (int) ((getEndTime() - new Date().getTime()) / 60000);
            lore.add(Lang.ATTACK_ICON_DESC_4.get(timeLeftEnd));
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
}
