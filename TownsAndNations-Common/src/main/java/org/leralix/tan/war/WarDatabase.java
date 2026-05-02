package org.leralix.tan.war;

import org.leralix.tan.data.DbManager;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.database.DatabaseData;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.WarGoal;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WarDatabase implements DatabaseData<WarData>, War {

    private final DbManager<WarData> manager;

    private WarData data;

    public WarDatabase(WarData data, DbManager<WarData> manager){
        this.manager = manager;
        this.data = data;
    }

    @Override
    public void setData(WarData data) {
        this.data = data;
    }

    @Override
    public String getID() {
        return data.getID();
    }

    @Override
    public String getName() {
        return data.getName();
    }

    @Override
    public void setName(String name) {
        mutate(war -> war.setName(name));
    }

    @Override
    public String getMainDefenderID() {
        return data.getMainDefenderID();
    }

    @Override
    public Territory getMainDefender() {
        return data.getMainDefender();
    }

    @Override
    public String getMainAttackerID() {
        return data.getMainAttackerID();
    }

    @Override
    public Territory getMainAttacker() {
        return data.getMainAttacker();
    }

    @Override
    public boolean isMainAttacker(Territory territory) {
        return data.isMainAttacker(territory);
    }

    @Override
    public boolean isMainDefender(Territory territory) {
        return data.isMainDefender(territory);
    }

    @Override
    public IconBuilder getIcon() {
        return data.getIcon();
    }

    @Override
    public void territorySurrender(Territory looserTerritory) {
        mutate(war -> war.territorySurrender(looserTerritory));
    }

    @Override
    public void territorySurrender(WarRole looserTerritory) {
        mutate(war -> war.territorySurrender(looserTerritory));
    }

    @Override
    public void endWar() {
        mutate(War::endWar);
    }

    @Override
    public Collection<PlannedAttack> getPlannedAttacks() {
        return data.getPlannedAttacks();
    }

    @Override
    public Map<String, PlannedAttack> getPlannedAttacksMap() {
        return data.getPlannedAttacksMap();
    }

    @Override
    public List<WarGoal> getGoals(WarRole warRole) {
        return data.getGoals(warRole);
    }

    @Override
    public void removeGoal(WarRole warRole, WarGoal goal) {
        mutate(war -> war.removeGoal(warRole, goal));
    }

    @Override
    public void addGoal(WarRole warRole, WarGoal conquerWarGoal) {
        mutate(war -> war.addGoal(warRole, conquerWarGoal));
    }

    @Override
    public Territory getTerritory(WarRole warRole) {
        return data.getTerritory(warRole);
    }

    @Override
    public Collection<FilledLang> generateWarGoalsDesciption(WarRole warRole, LangType langType) {
        var res = data.generateWarGoalsDesciption(warRole, langType);
        manager.save(data);
        return res;
    }

    @Override
    public void createPlannedAttack(WarRole roleOfAttacker, int startTime, int durationTime) {
        mutate(war -> war.createPlannedAttack(roleOfAttacker, startTime, durationTime));
    }

    @Override
    public Collection<String> getDefendersID() {
        return data.getDefendersID();
    }

    @Override
    public Collection<String> getAttackersID() {
        return data.getAttackersID();
    }

    @Override
    public WarRole getTerritoryRole(Territory territory) {
        return data.getTerritoryRole(territory);
    }

    @Override
    public WarRole getPlayerRole(ITanPlayer player) {
        return data.getPlayerRole(player);
    }

    @Override
    public void removeBelligerent(Territory territory) {
        mutate(war -> war.removeBelligerent(territory));
    }

    @Override
    public void addAttacker(Territory territory) {
        mutate(war -> war.addAttacker(territory));
    }

    @Override
    public void addDefender(Territory territory) {
        mutate(war -> war.addDefender(territory));
    }

    @Override
    public Collection<Territory> getDefendingTerritories() {
        return data.getDefendingTerritories();
    }

    @Override
    public Collection<Territory> getAttackingTerritories() {
        return data.getAttackingTerritories();
    }

    private synchronized void mutate(Consumer<War> action) {
        action.accept(data);
        manager.save(data);
    }
}
