package org.leralix.tan.wars.capture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.chunk.ClaimedChunk2;
import org.leralix.tan.dataclass.chunk.TerritoryChunk;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.NewClaimedChunkStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.wars.PlannedAttack;
import org.leralix.tan.wars.fort.Fort;

public class CaptureManager {

  private final Map<TerritoryChunk, CaptureChunk> captures = new HashMap<>();
  private final Map<String, CaptureFort> forts = new HashMap<>();

  private static CaptureManager instance;

  public static CaptureManager getInstance() {
    if (instance == null) {
      instance = new CaptureManager();
    }
    return instance;
  }

  public CaptureManager() {}

  public void updateCapture(PlannedAttack currentAttack) {
    handleFortCapture(currentAttack);
    handleChunkCapture(currentAttack);
  }

  private void handleFortCapture(PlannedAttack attackData) {
    for (Fort fortAtWar : attackData.getWar().getMainDefender().getOwnedForts()) {
      forts.putIfAbsent(
          fortAtWar.getID(),
          new CaptureFort(fortAtWar, attackData.getWar().getMainAttacker(), attackData.getID()));
    }

    registerPlayer(attackData.getAttackersPlayers(), forts, CaptureFort::addAttacker);
    registerPlayer(attackData.getDefendingPlayers(), forts, CaptureFort::addDefender);

    for (CaptureFort captureFort : forts.values()) {
      captureFort.update();
    }

    for (CaptureFort captureFort : forts.values()) {
      captureFort.clearPlayers();
    }
  }

  private void registerPlayer(
      Collection<ITanPlayer> players,
      Map<String, CaptureFort> forts,
      BiConsumer<CaptureFort, Player> consumer) {
    for (ITanPlayer tanPlayer : players) {
      Player player = tanPlayer.getPlayer();
      if (player == null || !player.isOnline()) continue;

      Vector3D playerPosition = new Vector3D(player.getLocation());

      for (CaptureFort captureFort : forts.values()) {
        if (captureFort.getFort().getPosition().getDistance(playerPosition)
            < Constants.getFortCaptureRadius()) {
          consumer.accept(captureFort, player);
        }
      }
    }
  }

  private void handleChunkCapture(PlannedAttack attackData) {
    for (CaptureChunk captureChunk : captures.values()) {
      captureChunk.resetPlayers();
    }

    Collection<ITanPlayer> attackers = attackData.getAttackersPlayers();
    Collection<ITanPlayer> defenders = attackData.getDefendingPlayers();
    TerritoryData mainAttacker = attackData.getWar().getMainAttacker();

    for (ITanPlayer attacker : attackers) {
      Player player = attacker.getPlayer();
      if (player == null || !player.isOnline()) {
        continue;
      }

      ClaimedChunk2 claimedChunk =
          NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());

      if (claimedChunk instanceof TerritoryChunk territoryChunk) {
        if (!canBeCaptured(
            territoryChunk,
            attackData.getWar().getMainAttacker(),
            attackData.getWar().getMainDefender())) {
          continue;
        }

        if (!captures.containsKey(territoryChunk)) {
          captures.putIfAbsent(
              territoryChunk,
              new CaptureChunk(0, territoryChunk, mainAttacker, attackData.getID()));
        }
        captures.get(territoryChunk).addAttacker(attacker.getPlayer());
      }
    }

    for (ITanPlayer defender : defenders) {
      Player player = defender.getPlayer();
      if (player == null || !player.isOnline()) {
        continue;
      }
      ClaimedChunk2 claimedChunk =
          NewClaimedChunkStorage.getInstance().get(player.getLocation().getChunk());
      if (claimedChunk instanceof TerritoryChunk territoryChunk) {

        if (!canBeCaptured(
            territoryChunk,
            attackData.getWar().getMainAttacker(),
            attackData.getWar().getMainDefender())) {
          continue;
        }

        if (!captures.containsKey(territoryChunk)) {
          captures.putIfAbsent(
              territoryChunk,
              new CaptureChunk(100, territoryChunk, mainAttacker, attackData.getID()));
        }
        captures.get(territoryChunk).addDefender(defender.getPlayer());
      }
    }

    for (CaptureChunk captureChunk : captures.values()) {
      captureChunk.update();
    }
  }

  private boolean canBeCaptured(
      TerritoryChunk territoryChunk, TerritoryData mainAttacker, TerritoryData mainDefender) {
    String ownerID = territoryChunk.getOwnerID();
    String occupierID = territoryChunk.getOccupierID();

    String defenderID = mainDefender.getID();
    String attackerID = mainAttacker.getID();

    if (ownerID.equals(attackerID) && occupierID.equals(defenderID)) {
      return true;
    }

    if (!ownerID.equals(defenderID)) {
      return false;
    }

    boolean surroundedBySame =
        NewClaimedChunkStorage.getInstance()
            .isAllAdjacentChunksClaimedBySameTerritory(territoryChunk.getChunk(), defenderID);

    return !surroundedBySame;
  }

  public void removeCapture(PlannedAttack plannedAttack) {
    String warID = plannedAttack.getID();

    Iterator<CaptureChunk> captureChunkIterator = captures.values().iterator();

    while (captureChunkIterator.hasNext()) {
      CaptureChunk captureChunk = captureChunkIterator.next();
      if (captureChunk.getWarID().equals(warID)) {
        captureChunk.warOver();
        captureChunkIterator.remove();
      }
    }

    Iterator<CaptureFort> captureFortIterator = forts.values().iterator();
    while (captureFortIterator.hasNext()) {
      CaptureFort captureFort = captureFortIterator.next();
      if (captureFort.getWarID().equals(warID)) {
        captureFort.warOver();
        captureFortIterator.remove();
      }
    }

    TerritoryData mainAttacker = plannedAttack.getWar().getMainAttacker();
    TerritoryData mainDefender = plannedAttack.getWar().getMainDefender();

    for (TerritoryChunk territoryChunk :
        NewClaimedChunkStorage.getInstance().getAllChunkFrom(mainAttacker)) {
      if (territoryChunk.isOccupied()
          && territoryChunk.getOccupierID().equals(mainDefender.getID())) {
        territoryChunk.liberate();
      }
    }

    for (TerritoryChunk territoryChunk :
        NewClaimedChunkStorage.getInstance().getAllChunkFrom(mainDefender)) {
      if (territoryChunk.isOccupied()
          && territoryChunk.getOccupierID().equals(mainAttacker.getID())) {
        territoryChunk.liberate();
      }
    }
  }
}
