package org.leralix.tan.war;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.timezone.TimeZoneEnum;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.war.attack.CurrentAttack;
import org.leralix.tan.war.info.AttackNotYetStarted;
import org.leralix.tan.war.info.AttackResult;
import org.leralix.tan.war.info.AttackResultCancelled;
import org.leralix.tan.war.info.WarRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    /**
     * The side declaring the war
     */
    private final WarRole warRole;

    private transient BukkitRunnable warStartTask;
    private transient BukkitRunnable warWarningTask;

    boolean isAdminApproved;

    /**
     * Constructor for a Planned attack
     *
     * @param id                    The ID of the attack
     * @param relatedWar            The war related to the attack
     * @param roleOfAttacker        The role of the attacker in the war. Can be Main attacker or Main defender
     * @param startTime             The delta time in minutes, starting in the current time.
     * @param endTime               The delta time in minutes between start and end of the attack.
     *                              If endTime is negative, attack will last forever
     */
    public PlannedAttack(String id, War relatedWar, WarRole roleOfAttacker, int startTime, int endTime) {
        this.ID = id;

        this.war = relatedWar;
        this.warID = war.getID();
        this.warRole = roleOfAttacker;


        this.isAdminApproved = !Constants.adminApprovalForStartOfAttack();

        this.startTime = System.currentTimeMillis() + startTime * 60 * 1000L;

        // If negative time, attack will last indefinitely
        if(endTime < 0){
            this.endTime = -1;
        }
        else {
            this.endTime = this.startTime + endTime * 60 * 1000L;
        }

        this.attackResult = new AttackNotYetStarted(this.startTime);

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
        long currentTime = System.currentTimeMillis();
        long timeLeftBeforeStart = (long) ((startTime - currentTime) * 0.02);
        long timeLeftBeforeWarning = timeLeftBeforeStart - 1200; //Warning 1 minute before


        if (timeLeftBeforeStart <= 0) {
            startAttack();
            return;
        }

        warStartTask = new BukkitRunnable() {
            @Override
            public void run() {
                startAttack();
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

    public void startAttack() {
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
        if (endTime < 0) {
            return false;
        }
        return System.currentTimeMillis() > endTime;
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
        // If in progress, start the attack until the preselected end date
        if(isInstantInAttack(System.currentTimeMillis())){
            startAttack();
            return;
        }
        // If the war finished while the server was offline and is still labeled "not started". Mark it as "cancelled"
        if(isFinished() && attackResult instanceof AttackNotYetStarted){
            end(new AttackResultCancelled());
            return;
        }
        // If attack has not yet started, start the countdown until start
        if (System.currentTimeMillis() < startTime){
            setUpStartOfAttack();
        }
    }


    public boolean isCancelled() {
        return attackResult instanceof AttackResultCancelled;
    }

    public WarRole getSideDeclaring() {
        return warRole;
    }

    public boolean isInstantInAttack(long epochMilli) {
        return epochMilli > startTime && (endTime < 0 || epochMilli < endTime);
    }
}
