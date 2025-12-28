package org.leralix.tan.war;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.timezone.TimeZoneEnum;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.info.AttackNotYetStarted;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.AttackResultCancelled;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.CurrentAttack;
import org.leralix.tan.war.legacy.WarRole;

import java.util.*;

public class PlannedAttack {

    private final String ID;
    private AttackResult attackResult;

    /**
     * The start time of the war, in milliseconds since January 1, 1970
     */
    final long startTime;
    /**
     * The end time of the war, in milliseconds since January 1, 1970
     */
    private final long endTime;

    /**
     * used to recover War after plugin reload
     */
    private final String warID;
    private transient War war;

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
        this.warID = war.getID();
        this.warRole = createAttackData.getAttackingSide();


        this.isAdminApproved = !Constants.adminApprovalForStartOfAttack();

        this.startTime = System.currentTimeMillis() + (long) createAttackData.getSelectedTime() * 60 * 1000;
        this.endTime = this.startTime + Constants.getAttackDuration() * 60 * 1000;

        this.attackResult = new AttackNotYetStarted(startTime);

        setUpStartOfAttack();
    }

    public String getID() {
        return ID;
    }

    public War getWar() {
        if(war == null){
            war = WarStorage.getInstance().get(warID);
        }
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
        for (TerritoryData defendingTerritory : getWar().getDefendingTerritories()) {
            defenders.addAll(defendingTerritory.getITanPlayerList());
        }
        return defenders;
    }

    public Collection<ITanPlayer> getAttackersPlayers() {
        Collection<ITanPlayer> defenders = new ArrayList<>();
        for (TerritoryData attackingTerritory : getWar().getAttackingTerritories()) {
            defenders.addAll(attackingTerritory.getITanPlayerList());
        }
        return defenders;
    }

    public void broadCastMessageWithSound(FilledLang message, SoundEnum soundEnum) {
        Collection<TerritoryData> territoryData = getWar().getAttackingTerritories();
        territoryData.addAll(getWar().getDefendingTerritories());
        for (TerritoryData territory : territoryData) {
            territory.broadcastMessageWithSound(message, soundEnum);
        }
    }

    public void setUpStartOfAttack() {

        //Conversion seconds -> ticks
        long currentTime = new Date().getTime();
        long timeLeftBeforeStart = (long) ((startTime - currentTime) * 0.02);
        long timeLeftBeforeWarning = timeLeftBeforeStart - 1200; //Warning 1 minute before


        if (timeLeftBeforeStart <= 0) {
            startWar();
            return;
        }

        warStartTask = new BukkitRunnable() {
            @Override
            public void run() {
                startWar();
            }
        };
        warStartTask.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeStart);

        if (timeLeftBeforeWarning > 0) {
            warWarningTask = new BukkitRunnable() {
                @Override
                public void run() {
                    broadCastMessageWithSound(Lang.ATTACK_START_IN_1_MINUTES.get(getWar().getName()), SoundEnum.WAR);
                }
            };
            warWarningTask.runTaskLater(TownsAndNations.getPlugin(), timeLeftBeforeWarning);
        }
    }

    public void startWar() {
        broadCastMessageWithSound(Lang.ATTACK_START_NOW.get(getWar().getName()), SoundEnum.WAR);
        CurrentAttacksStorage.startAttack(this, endTime);
    }

    public IconBuilder getAdminIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZoneEnum) {

        IconBuilder iconBuilder = getIcon(iconManager, langType, timeZoneEnum);

        if (isAdminApproved) {
            return iconBuilder.setClickToAcceptMessage(Lang.ATTACK_ICON_DESC_ADMIN_APPROVED);
        } else {


            return iconBuilder
                    .addDescription(
                            Lang.ATTACK_ICON_DESC_ADMIN_NOT_APPROVED.get(),
                            Lang.ATTACK_WILL_NOT_TRIGGER_IF_NOT_APPROVED.get()
                    )
                    .setClickToAcceptMessage(
                            Lang.LEFT_CLICK_TO_AUTHORIZE,
                            Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE
                    );
        }
    }

    public IconBuilder getIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZone) {
        return iconManager.get(Material.IRON_SWORD)
                .setName(ChatColor.GREEN + getWar().getName())
                .setDescription(
                        Lang.ATTACK_ICON_DESC_1.get(getWar().getMainAttacker().getColoredName()),
                        Lang.ATTACK_ICON_DESC_2.get(getWar().getMainDefender().getColoredName()),
                        Lang.ATTACK_ICON_DESC_3.get(Integer.toString(getWar().getAttackersID().size())),
                        Lang.ATTACK_ICON_DESC_4.get(Integer.toString(getWar().getDefendersID().size()))
                )
                .addDescription(attackResult.getResultLines(langType, timeZone));
    }

    public IconBuilder getIcon(IconManager iconManager, LangType langType, TimeZoneEnum timeZone, TerritoryData territoryConcerned) {
        return getIcon(iconManager, langType, timeZone)
                .addDescription(Lang.ATTACK_ICON_DESC_8.get(getWar().getTerritoryRole(territoryConcerned).getName(langType)));
    }

    /**
     * Called at the end of a planned attack
     * @param attackResult The result of the attack
     */
    public void end(AttackResult attackResult) {

        this.attackResult = attackResult;

        if (warStartTask != null) {
            warStartTask.cancel();
        }
        if (warWarningTask != null) {
            warWarningTask.cancel();
        }

        CurrentAttack currentAttack = CurrentAttacksStorage.get(ID);
        if (currentAttack != null) {
            currentAttack.end();
        }
    }

    public WarRole getRole(ITanPlayer player) {
        for (TerritoryData territoryData : player.getAllTerritoriesPlayerIsIn()) {
            WarRole role = getWar().getTerritoryRole(territoryData);
            if (role != WarRole.NEUTRAL) {
                return role;
            }
        }
        return WarRole.NEUTRAL;
    }

    public List<OfflinePlayer> getAllOfflinePlayers() {
        List<ITanPlayer> res = new ArrayList<>(getDefendingPlayers());
        res.addAll(getAttackersPlayers());
        return res.stream().map(ITanPlayer::getOfflinePlayer).filter(Objects::nonNull).toList();
    }

    public List<Player> getAllOnlinePlayers() {
        return getAllOfflinePlayers().stream().map(OfflinePlayer::getPlayer).filter(Objects::nonNull).toList();
    }

    /**
     * Check if the attack is finished
     * @return true if the attack is finished
     */
    public boolean isFinished(){
        return System.currentTimeMillis() > endTime;
    }

    /**
     * Check if the attack is in progress by checking the current time
     * @return true if the attack is in progress
     */
    public boolean isInProgress() {
        return System.currentTimeMillis() >= startTime && !isFinished();
    }

    /**
     * @return true if the attack has not yet started
     */
    public boolean isNotStarted() {
        return System.currentTimeMillis() < startTime;
    }

    /**
     * Update the status of the attack, starting or ending it if necessary
     */
    public void updateStatus() {
        if(isInProgress()){
            startWar();
        }
        else if(isFinished() && attackResult instanceof AttackNotYetStarted){
            end(new AttackResultCancelled());
        }
    }


    public boolean isCancelled() {
        return attackResult instanceof AttackResultCancelled;
    }
}
