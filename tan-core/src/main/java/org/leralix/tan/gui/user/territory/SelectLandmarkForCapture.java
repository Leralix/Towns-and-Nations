package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.WarRole;
import org.leralix.tan.wars.legacy.wargoals.CaptureLandmarkWarGoal;

public class SelectLandmarkForCapture extends IteratorGUI {

  private final WarRole warRole;
  private final TerritoryData territoryData;
  private final War war;

  private final TownData enemyTownData;

  private SelectLandmarkForCapture(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData, War war, WarRole warRole) {
    super(player, tanPlayer, Lang.HEADER_SELECT_WARGOAL.get(tanPlayer.getLang()), 3);
    this.warRole = warRole;
    this.territoryData = territoryData;
    this.war = war;
    this.enemyTownData =
        (TownData)
            (war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker());
  }

  public static void open(Player player, TerritoryData territoryData, War war, WarRole warRole) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new SelectLandmarkForCapture(player, tanPlayer, territoryData, war, warRole).open();
            });
  }

  @Override
  public void open() {
    iterator(getLandmarks(langType), p -> ChooseWarGoal.open(p, territoryData, war, warRole));
    gui.open(player);
  }

  private List<GuiItem> getLandmarks(LangType langType) {

    List<GuiItem> items = new ArrayList<>();

    for (Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(enemyTownData)) {

      GuiItem item =
          iconManager
              .get(landmark.getIcon(langType))
              .setName(landmark.getName())
              .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
              .setAction(
                  event -> {
                    war.addGoal(warRole, new CaptureLandmarkWarGoal(landmark));
                    SelectWarGoals.open(player, territoryData, war, warRole);
                  })
              .asGuiItem(player, langType);
      items.add(item);
    }
    return items;
  }
}
