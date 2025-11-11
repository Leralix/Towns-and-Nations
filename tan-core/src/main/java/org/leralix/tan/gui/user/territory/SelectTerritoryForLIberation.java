package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.WarRole;
import org.leralix.tan.wars.legacy.wargoals.LiberateWarGoal;

public class SelectTerritoryForLIberation extends IteratorGUI {

  private final WarRole warRole;
  private final TerritoryData territoryData;
  private final War war;

  private final TerritoryData enemyTerritory;

  private SelectTerritoryForLIberation(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData, War war, WarRole warRole) {
    super(player, tanPlayer, Lang.HEADER_SELECT_WARGOAL.get(tanPlayer.getLang()), 3);
    this.warRole = warRole;
    this.territoryData = territoryData;
    this.war = war;
    this.enemyTerritory =
        war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker();
  }

  public static void open(Player player, TerritoryData territoryData, War war, WarRole warRole) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new SelectTerritoryForLIberation(player, tanPlayer, territoryData, war, warRole)
                  .open();
            });
  }

  @Override
  public void open() {
    iterator(getTerritoryToLiberate(), p -> ChooseWarGoal.open(p, territoryData, war, warRole));
    gui.open(player);
  }

  private List<GuiItem> getTerritoryToLiberate() {
    List<GuiItem> items = new ArrayList<>();

    for (TerritoryData territory : enemyTerritory.getVassals()) {

      if (territory.isCapital()) {
        continue;
      }

      items.add(
          iconManager
              .get(territory.getIcon())
              .setName(territory.getName())
              .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
              .setAction(
                  action -> {
                    SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                    war.addGoal(warRole, new LiberateWarGoal(territory));
                    SelectWarGoals.open(player, territoryData, war, warRole);
                  })
              .asGuiItem(player, langType));
    }
    return items;
  }
}
