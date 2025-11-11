package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.building.Building;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.CreateFortEvent;
import org.leralix.tan.listeners.interact.events.property.CreateTerritoryPropertyEvent;
import org.leralix.tan.upgrade.rewards.numeric.PropertyCap;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

public class BuildingMenu extends IteratorGUI {

  private final BasicGui previousMenu;
  private final TerritoryData territoryData;

  public BuildingMenu(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData, BasicGui previousMenu) {
    super(player, tanPlayer, Lang.HEADER_BUILDING_MENU.get(player), 4);
    this.territoryData = territoryData;
    this.previousMenu = previousMenu;
    // open() doit être appelé explicitement après la construction pour respecter le modèle
    // asynchrone
  }

  @Override
  public void open() {
    iterator(getBuildings(), p -> previousMenu.open());

    // For now, only towns can have properties
    if (territoryData instanceof TownData townData) {
      gui.setItem(4, 4, getCreatePublicPropertyButton(townData));
    }
    gui.setItem(4, 5, getCreateFortButton());
    gui.open(player);
  }

  private @NotNull GuiItem getCreatePublicPropertyButton(TownData townData) {

    List<FilledLang> description = new ArrayList<>();

    int nbProperties = townData.getProperties().size();
    int maxNbProperties = townData.getNewLevel().getStat(PropertyCap.class).getMaxAmount();
    if (nbProperties >= maxNbProperties) {
      description.add(
          Lang.GUI_PROPERTY_CAP_FULL.get(
              Integer.toString(nbProperties), Integer.toString(maxNbProperties)));
    } else {
      description.add(
          Lang.GUI_PROPERTY_CAP.get(
              Integer.toString(nbProperties), Integer.toString(maxNbProperties)));
    }
    description.add(Lang.CREATE_PUBLIC_PROPERTY_COST.get());

    return iconManager
        .get(IconKey.PLAYER_PROPERTY_ICON)
        .setName(Lang.CREATE_PUBLIC_PROPERTY_ICON.get(langType))
        .setDescription(description)
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
        .setAction(
            action -> {
              if (!townData.doesPlayerHavePermission(player, RolePermission.MANAGE_PROPERTY)) {
                TanChatUtils.message(
                    player, Lang.PLAYER_NO_PERMISSION.get(langType), SoundEnum.NOT_ALLOWED);
                return;
              }
              if (nbProperties >= maxNbProperties) {
                TanChatUtils.message(
                    player,
                    Lang.GUI_PROPERTY_CAP_FULL.get(
                        langType,
                        Integer.toString(nbProperties),
                        Integer.toString(maxNbProperties)),
                    SoundEnum.NOT_ALLOWED);
                return;
              }
              RightClickListener.register(
                  player, new CreateTerritoryPropertyEvent(player, townData));
            })
        .asGuiItem(player, langType);
  }

  private @NotNull GuiItem getCreateFortButton() {
    return iconManager
        .get(IconKey.FORT_BUILDING_ICON)
        .setName(Lang.CREATE_FORT_ICON.get(langType))
        .setDescription(Lang.CREATE_FORT_DESC1.get(Double.toString(Constants.getFortCost())))
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
        .setAction(
            action -> {
              if (Constants.getFortCost() > territoryData.getBalance()) {
                TanChatUtils.message(
                    player,
                    Lang.TERRITORY_NOT_ENOUGH_MONEY.get(
                        langType,
                        territoryData.getColoredName(),
                        Double.toString(Constants.getFortCost() - territoryData.getBalance())));
                return;
              }

              TanChatUtils.message(player, Lang.RIGHT_CLICK_TO_PLACE_FORT.get(langType));
              RightClickListener.register(player, new CreateFortEvent(territoryData));
              player.closeInventory();
            })
        .asGuiItem(player, langType);
  }

  private List<GuiItem> getBuildings() {
    List<GuiItem> res = new ArrayList<>();
    for (Building building : territoryData.getBuildings()) {
      res.add(building.getGuiItem(iconManager, player, this, langType));
    }
    return res;
  }
}
