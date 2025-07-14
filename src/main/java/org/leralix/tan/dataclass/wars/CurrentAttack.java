package org.leralix.tan.dataclass.wars;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.lib.position.Vector3D;
import org.leralix.lib.utils.ParticleUtils;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.StrongholdData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.wars.wargoals.WarGoal;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.AttackWonByAttackerInternalEvent;
import org.leralix.tan.events.events.AttackWonByDefenderInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.ProgressBar;

import java.util.UUID;

public class CurrentAttack {

    private final PlannedAttack attackData;


    private int score = 500;
    private static final int MIN_SCORE = 0;

    private static final int MAX_SCORE = 1000;
    private long remainingTime;
    private BossBar bossBar;
    private String originalTitle;
    private final StrongholdData defenderStronghold;

    StrongholdListener strongholdListener;


    public CurrentAttack(PlannedAttack plannedAttack) {

        this.attackData = plannedAttack;


        this.originalTitle = "War start";
        long warDuration = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("WarDuration");
        this.remainingTime = warDuration * 60 * 20;

        this.bossBar = Bukkit.createBossBar(this.originalTitle, BarColor.RED, BarStyle.SOLID);
        this.defenderStronghold = plannedAttack.getDefenderStronghold();
        this.defenderStronghold.setHolderSide(AttackSide.DEFENDER);
        this.defenderStronghold.setControlLevel(0);
        this.strongholdListener = new StrongholdListener(this, defenderStronghold);

        for (TerritoryData territoryData : plannedAttack.getAttackingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                tanPlayer.addWar(this);
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }
        for (TerritoryData territoryData : plannedAttack.getDefendingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                tanPlayer.addWar(this);
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    bossBar.addPlayer(player);
                }
            }
        }
        startTimer();

    }

    private void updateBossBar() {
        long hours = remainingTime / 72000;
        long minutes = (remainingTime % 72000) / 1200;
        long seconds = (remainingTime % 1200) / 20;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        bossBar.setTitle(Lang.TITLE_ATTACK.get(originalTitle, timeString));
        bossBar.setProgress((double) score / MAX_SCORE);
    }


    public void playerKilled(ITanPlayer tanPlayer, Player killer) {
        AttackSide attackSide = getSideOfPlayer(tanPlayer);

        if (attackSide == AttackSide.ATTACKER) {
            if (killer != null) {
                attackingLoss();
            } else {
                Location playerLocation = tanPlayer.getPlayer().getLocation();
                ClaimedChunk2 claimedChunk = NewClaimedChunkStorage.getInstance().get(playerLocation.getChunk());
                if (attackData.getDefendingTerritories().contains(claimedChunk.getOwner())) {
                    attackingLoss();
                }
            }
        }
        if (attackSide == AttackSide.DEFENDER && killer != null) {
            defendingLoss();
        }

    }

    public void attackingLoss() {
        int nbAttackers = getNumberOfOnlineAttackers();
        if (nbAttackers == 0) {
            return;
        }
        double multiplier = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("warScoreMultiplier");
        int deltaScore = (int) (multiplier / nbAttackers * 500);
        addScore(-deltaScore);
        sendChatMessage("Attacking player killed !");
    }

    public void defendingLoss() {
        int nbDefenders = getNumberOfOnlineDefenders();
        if (nbDefenders == 0) {
            return;
        }
        double multiplier = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getDouble("warScoreMultiplier");
        int deltaScore = (int) (multiplier / nbDefenders * 500);
        addScore(deltaScore);
        sendChatMessage("Defensive player killed!");
    }

    private int getNumberOfOnlineDefenders() {
        int sum = 0;
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            for (String playerID : territoryData.getPlayerIDList()) {
                if (Bukkit.getPlayer(UUID.fromString(playerID)) != null)
                    sum++;
            }
        }
        return sum;
    }

    private int getNumberOfOnlineAttackers() {
        int sum = 0;
        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            for (String playerID : territoryData.getPlayerIDList()) {
                if (Bukkit.getPlayer(UUID.fromString(playerID)) != null)
                    sum++;
            }
        }
        return sum;
    }

    private void setBossBarTitle(String title) {
        originalTitle = title;
    }

    private void addScore(int score) {
        this.score += score;
        if (this.score >= MAX_SCORE) {
            this.score = MAX_SCORE;
        }
        if (this.score <= MIN_SCORE) {
            this.score = MIN_SCORE;
        }
        updateBossBar();
    }

    private void attackerWin() {

        EventManager.getInstance().callEvent(new AttackWonByAttackerInternalEvent(attackData.getMainDefender(), attackData.getMainAttacker()));

        WarGoal warGoal = attackData.getWarGoal();

        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    warGoal.sendAttackSuccessToAttackers(player);
                }
            }
        }
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    warGoal.sendAttackSuccessToDefenders(player);
                }
            }
        }
        warGoal.applyWarGoal();
        bossBar.setTitle(Lang.WAR_ATTACKER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    private void defenderWin() {

        EventManager.getInstance().callEvent(new AttackWonByDefenderInternalEvent(attackData.getMainDefender(), attackData.getMainAttacker()));

        WarGoal warGoal = attackData.getWarGoal();

        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    warGoal.sendAttackFailedToDefender(player);
                }
            }
        }
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    warGoal.sendAttackFailedToAttacker(player);
                }
            }
        }

        bossBar.setTitle(Lang.WAR_DEFENDER_WON_ANNOUNCEMENT.get());
        endWar();
    }

    public void addPlayer(ITanPlayer tanPlayer) {
        Player player = tanPlayer.getPlayer();
        if (player != null && remainingTime > 0 && score > 0 && score < MAX_SCORE) {
            bossBar.addPlayer(player);
        }
    }

    private void startTimer() {
        BukkitRunnable timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (remainingTime > 0 && score > 0 && score < MAX_SCORE) {
                    remainingTime--;
                    updateBossBar();
                } else {
                    if (score >= MAX_SCORE)
                        attackerWin();
                    else
                        defenderWin();
                    this.cancel();
                    strongholdListener.stop();
                }
            }
        };
        timerTask.runTaskTimer(TownsAndNations.getPlugin(), 0, 1); // Exécute toutes les secondes
    }


    private void endWar() {

        new BukkitRunnable() {
            @Override
            public void run() {
                for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
                    for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                        tanPlayer.removeWar(CurrentAttack.this);
                    }
                }
                for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
                    for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                        tanPlayer.removeWar(CurrentAttack.this);
                    }
                }

                bossBar.removeAll();
                bossBar = null;
                CurrentAttacksStorage.remove(CurrentAttack.this);

                for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
                    territoryData.removeCurrentAttack(CurrentAttack.this);
                }
                for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
                    territoryData.removeCurrentAttack(CurrentAttack.this);
                }

            }
        }.runTaskLater(TownsAndNations.getPlugin(), 20L * 20); //Still showing the boss bar for 20s
    }

    public boolean containsPlayer(ITanPlayer tanPlayer) {
        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return true;
            }
        }
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return true;
            }
        }
        return false;
    }


    public void updateControl() {
        String controlSide = defenderStronghold.getHolderSide().getBossBarMessage();
        String progressBar = ProgressBar.createProgressBar(defenderStronghold.getControlLevel(), 10, 20, ChatColor.RED, ChatColor.GREEN);

        setBossBarTitle(controlSide + ChatColor.WHITE + " | " + progressBar);
    }

    public AttackSide getSideOfPlayer(ITanPlayer tanPlayer) {
        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return AttackSide.ATTACKER;
            }
        }
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            if (territoryData.isPlayerIn(tanPlayer)) {
                return AttackSide.DEFENDER;
            }
        }
        return null;
    }

    public void addScoreOfStronghold() {
        AttackSide attackSide = this.defenderStronghold.getHolderSide();

        if (attackSide == AttackSide.ATTACKER) {
            addScore(5);
        }
        if (attackSide == AttackSide.DEFENDER) {
            addScore(-5);
        }
    }

    public void sendChatMessage(String message) {
        for (TerritoryData territoryData : attackData.getAttackingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(message));
                }
            }
        }
        for (TerritoryData territoryData : attackData.getDefendingTerritories()) {
            for (ITanPlayer tanPlayer : territoryData.getITanPlayerList()) {
                Player player = tanPlayer.getPlayer();
                if (player != null) {
                    player.spigot().sendMessage(ChatMessageType.CHAT, new TextComponent(message));
                }
            }
        }
    }

    public PlannedAttack getAttackData() {
        return attackData;
    }

    public void displayBoundaries() {
        for (Player player : attackData.getAllPlayers()) {
            displayBoundaries(player);
        }
    }

    public void displayBoundaries(Player player) {
        Chunk centerChunk = player.getLocation().getChunk();
        World world = centerChunk.getWorld();
        String worldID = world.getUID().toString();
        int centerChunkX = centerChunk.getX();
        int centerChunkZ = centerChunk.getZ();
        int viewDistance = 4;

        for (int dx = -viewDistance; dx <= viewDistance; dx++) {
            for (int dz = -viewDistance; dz <= viewDistance; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;

                if (!isOwnedByDefensiveSide(chunkX, chunkZ, worldID)) {
                    continue;
                }

                int x0 = chunkX * 16;
                int z0 = chunkZ * 16;
                int y = (int) player.getLocation().getY() + 1;

                // NORTH
                if (!isOwnedByDefensiveSide(chunkX, chunkZ - 1, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0, y, z0, worldID),
                            new Vector3D(x0 + 16, y, z0, worldID),
                            1);
                }

                // SOUTH
                if (!isOwnedByDefensiveSide(chunkX, chunkZ + 1, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0, y, z0 + 16, worldID),
                            new Vector3D(x0 + 16, y, z0 + 16, worldID),
                            1);
                }

                // WEST
                if (!isOwnedByDefensiveSide(chunkX - 1, chunkZ, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0, y, z0, worldID),
                            new Vector3D(x0, y, z0 + 16, worldID),
                            1);
                }

                // EAST
                if (!isOwnedByDefensiveSide(chunkX + 1, chunkZ, worldID)) {
                    ParticleUtils.drawLine(TownsAndNations.getPlugin(), player,
                            new Vector3D(x0 + 16, y, z0, worldID),
                            new Vector3D(x0 + 16, y, z0 + 16, worldID),
                            1);
                }
            }
        }
    }

    private boolean isOwnedByDefensiveSide(int x, int z, String worldID) {
        ClaimedChunk2 claimedChunk2 = NewClaimedChunkStorage.getInstance().get(x, z, worldID);

        return claimedChunk2 instanceof TerritoryChunk territoryChunk &&
                territoryChunk.getOccupierID().equals(attackData.getMainDefender().getID()) &&
                territoryChunk.isOccupied();
    }
}
