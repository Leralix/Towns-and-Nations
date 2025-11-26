package org.leralix.tan.gui.user.territory.relation;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ActiveTruce;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.storage.stored.TruceStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.RelationConstant;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class AddRelationMenu extends IteratorGUI {

  private final TerritoryData territoryData;
  private final TownRelation wantedRelation;
  private final Map<String, ITanPlayer> playersData;

  private AddRelationMenu(
      Player player,
      ITanPlayer tanPlayer,
      TerritoryData territory,
      TownRelation wantedRelation,
      Map<String, ITanPlayer> playersData) {
    super(
        player,
        tanPlayer,
        Lang.HEADER_SELECT_ADD_TERRITORY_RELATION.get(
            tanPlayer.getLang(), wantedRelation.getName(tanPlayer.getLang())),
        6);
    this.territoryData = territory;
    this.wantedRelation = wantedRelation;
    this.playersData = playersData;
  }

  public static void open(Player player, TerritoryData territoryData, TownRelation wantedRelation) {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();

    storage
        .get(player)
        .thenCompose(
            tanPlayer -> {
              // Load all online players data in parallel
              List<String> playerIDs = new ArrayList<>();
              playerIDs.addAll(
                  TownDataStorage.getInstance().getAllSync().values().stream()
                      .flatMap(town -> town.getPlayerIDList().stream())
                      .toList());
              playerIDs.addAll(
                  RegionDataStorage.getInstance().getAllSync().values().stream()
                      .flatMap(region -> region.getPlayerIDList().stream())
                      .toList());

              List<CompletableFuture<ITanPlayer>> playerFutures = new ArrayList<>();
              for (String playerID : playerIDs) {
                playerFutures.add(storage.get(playerID));
              }

              return CompletableFuture.allOf(playerFutures.toArray(new CompletableFuture[0]))
                  .thenApply(
                      v -> {
                        Map<String, ITanPlayer> playersData = new HashMap<>();
                        for (CompletableFuture<ITanPlayer> future : playerFutures) {
                          ITanPlayer playerData = future.join();
                          playersData.put(playerData.getID(), playerData);
                        }
                        return new Object[] {tanPlayer, playersData};
                      });
            })
        .thenAccept(
            data -> {
              ITanPlayer tanPlayer = (ITanPlayer) ((Object[]) data)[0];
              @SuppressWarnings("unchecked")
              Map<String, ITanPlayer> playersData = (Map<String, ITanPlayer>) ((Object[]) data)[1];
              new AddRelationMenu(player, tanPlayer, territoryData, wantedRelation, playersData)
                  .open();
            });
  }

  @Override
  public void open() {

    iterator(
        getTerritories(),
        p -> OpenRelationMenu.open(p, territoryData, wantedRelation),
        Material.GREEN_STAINED_GLASS_PANE);

    gui.open(player);
  }

  private List<GuiItem> getTerritories() {
    ITanPlayer tanPlayer = playersData.get(player.getUniqueId().toString());

    List<String> relationListID =
        territoryData.getRelations().getTerritoriesIDWithRelation(wantedRelation);
    List<GuiItem> guiItems = new ArrayList<>();

    List<String> territories = new ArrayList<>();
    territories.addAll(TownDataStorage.getInstance().getAllSync().keySet());
    territories.addAll(RegionDataStorage.getInstance().getAllSync().keySet());

    territories.removeAll(relationListID); // Territory already have this relation
    territories.remove(territoryData.getID()); // Remove itself

    for (String otherTownUUID : territories) {
      TerritoryData otherTerritory = TerritoryUtil.getTerritory(otherTownUUID);
      ItemStack icon =
          otherTerritory.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang());

      TownRelation actualRelation = territoryData.getRelationWith(otherTerritory);

      if (!actualRelation.canBeChanged()) {
        continue;
      }

      GuiItem iconGui =
          ItemBuilder.from(icon)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);

                    if (otherTerritory.haveNoLeader()) {
                      TanChatUtils.message(
                          player, Lang.TOWN_DIPLOMATIC_INVITATION_NO_LEADER.get(tanPlayer));
                      return;
                    }

                    if (wantedRelation.isSuperiorTo(actualRelation)) {
                      otherTerritory.receiveDiplomaticProposal(territoryData, wantedRelation);
                      TanChatUtils.message(
                          player,
                          Lang.DIPLOMATIC_INVITATION_SENT_SUCCESS.get(
                              tanPlayer, otherTerritory.getName()));
                    } else {
                      RelationConstant relationConstant =
                          Constants.getRelationConstants(actualRelation);
                      int trucePeriod = relationConstant.trucePeriod();
                      // If actual relation has a truce, it cannot be switched to a negative
                      // relation instantly
                      if (wantedRelation.isNegative()) {
                        if (trucePeriod > 0) {
                          TanChatUtils.message(
                              player,
                              Lang.CURRENT_RELATION_REQUIRES_TRUCE.get(
                                  tanPlayer, Integer.toString(trucePeriod)),
                              NOT_ALLOWED);
                          return;
                        }
                        long nbActiveHourTruce =
                            TruceStorage.getInstance()
                                .getRemainingTruce(territoryData, otherTerritory);
                        if (nbActiveHourTruce > 0) {
                          TanChatUtils.message(
                              player,
                              Lang.CANNOT_SET_RELATION_TO_NEGATIVE_WHILE_TRUCE.get(
                                  tanPlayer,
                                  Long.toString(nbActiveHourTruce),
                                  otherTerritory.getColoredName()),
                              NOT_ALLOWED);
                          return;
                        }
                      }

                      // Successfully switched to a new relation. If old relation required a truce,
                      // apply it.
                      ActiveTruce activeTruce =
                          new ActiveTruce(territoryData, otherTerritory, trucePeriod);
                      TruceStorage.getInstance().add(activeTruce);
                      territoryData.setRelation(otherTerritory, wantedRelation);
                    }
                    OpenRelationMenu.open(player, territoryData, wantedRelation);
                  });
      guiItems.add(iconGui);
    }

    return guiItems;
  }
}
