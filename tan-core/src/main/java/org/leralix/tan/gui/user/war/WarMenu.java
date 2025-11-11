package org.leralix.tan.gui.user.war;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.SelectWarGoals;
import org.leralix.tan.gui.user.territory.WarsMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.WarRole;
import org.leralix.tan.wars.legacy.wargoals.WarGoal;

public class WarMenu extends BasicGui {

  private final TerritoryData territoryData;
  private final War war;
  private final WarRole warRole;

  private WarMenu(Player player, ITanPlayer tanPlayer, TerritoryData territoryData, War war) {
    super(player, tanPlayer, "War Menu", 3);
    this.territoryData = territoryData;
    this.war = war;
    this.warRole =
        war.isMainAttacker(territoryData) ? WarRole.MAIN_ATTACKER : WarRole.MAIN_DEFENDER;
  }

  public static void open(Player player, TerritoryData territoryData, War war) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new WarMenu(player, tanPlayer, territoryData, war).open();
            });
  }

  @Override
  public void open() {
    gui.setItem(1, 5, getWarIcon());
    gui.setItem(2, 3, getWargoalsButton());
    gui.setItem(2, 4, getAttackButton());
    gui.setItem(2, 6, getEnemyWargoalsIcon());
    gui.setItem(2, 7, getSurrenderButton());
    gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> territoryData.openMainMenu(player)));
    gui.open(player);
  }

  private GuiItem getWarIcon() {

    return iconManager
        .get(war.getIcon())
        .setName(war.getName())
        .setDescription(
            Lang.ATTACK_ICON_DESC_1.get(war.getMainAttacker().getColoredName()),
            Lang.ATTACK_ICON_DESC_2.get(war.getMainDefender().getColoredName()))
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getWargoalsButton() {

    List<FilledLang> description = new ArrayList<>();
    description.add(Lang.WAR_GOAL_LIST_BUTTON_DESC1.get());
    for (WarGoal goal : war.getGoals(warRole)) {
      description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(goal.getCurrentDesc(langType)));
    }

    // If no goals are set, add a message
    if (description.size() == 1) {
      description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get());
    }

    return iconManager
        .get(IconKey.WAR_GOAL_LIST_ICON)
        .setName(Lang.WAR_GOAL_LIST_BUTTON.get(langType))
        .setDescription(description)
        .setAction(action -> SelectWarGoals.open(player, territoryData, war, warRole))
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getAttackButton() {
    return iconManager
        .get(IconKey.WAR_CREATE_ATTACK_ICON)
        .setName(Lang.WAR_CREATE_ATTACK.get(langType))
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
        .setAction(action -> CreateAttackMenu.open(player, territoryData, war, warRole))
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getEnemyWargoalsIcon() {

    List<FilledLang> description = new ArrayList<>();
    description.add(Lang.WAR_ENEMY_GOAL_LIST_DESC1.get());
    for (WarGoal goal : war.getGoals(warRole.opposite())) {
      description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST.get(goal.getCurrentDesc(langType)));
    }

    // If no goals are set, add a message
    if (description.size() == 1) {
      description.add(Lang.WAR_GOAL_LIST_BUTTON_LIST_NO_WAR_GOAL_SET.get());
    }

    return iconManager
        .get(IconKey.WAR_ENEMY_GOAL_LIST_ICON)
        .setName(Lang.WAR_ENEMY_GOAL_LIST.get(langType))
        .setDescription(description)
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getSurrenderButton() {

    List<FilledLang> description = new ArrayList<>();
    description.add(Lang.WAR_SURRENDER_DESC1.get());
    description.add(Lang.WAR_SURRENDER_DESC2.get());

    description.addAll(war.generateWarGoalsDesciption(warRole, langType));

    return iconManager
        .get(IconKey.WAR_SURRENDER_ICON)
        .setName(Lang.WAR_SURRENDER.get(langType))
        .setDescription(description)
        .setAction(
            action -> {
              war.territorySurrender(warRole);
              WarsMenu.open(player, territoryData);
            })
        .asGuiItem(player, langType);
  }
}
