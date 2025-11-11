package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.interact.RightClickListener;
import org.leralix.tan.listeners.interact.events.property.CreatePlayerPropertyEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.upgrade.rewards.numeric.PropertyCap;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerPropertiesMenu extends IteratorGUI {

  private PlayerPropertiesMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_PLAYER_PROPERTIES.get(tanPlayer.getLang()), 3);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new PlayerPropertiesMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {

    GuiUtil.createIterator(
        gui, getProperties(), page, player, PlayerMenu::open, p -> nextPage(), p -> previousPage());

    gui.setItem(3, 5, getNewPropertyButton());

    gui.open(player);
  }

  private GuiItem getNewPropertyButton() {

    List<FilledLang> description = new ArrayList<>();
    List<IndividualRequirement> requirements = new ArrayList<>();
    if (tanPlayer.hasTown()) {
      TownData townData = tanPlayer.getTownSync();

      double costPerBlock = townData.getTaxOnCreatingProperty();

      if (costPerBlock > 0) {
        description.add(Lang.GUI_PROPERTY_COST_PER_BLOCK.get(Double.toString(costPerBlock)));
      }
      requirements.add(townData.getNewLevel().getStat(PropertyCap.class).getRequirement(townData));
      requirements.add(
          new RankPermissionRequirement(townData, tanPlayer, RolePermission.CREATE_PROPERTY));
    } else {
      description.add(Lang.PLAYER_NO_TOWN.get());
    }

    return iconManager
        .get(IconKey.CREATE_NEW_PROPERTY_ICON)
        .setName(Lang.GUI_PLAYER_NEW_PROPERTY.get(tanPlayer))
        .setDescription(description)
        .setRequirements(requirements)
        .setAction(
            event -> {
              if (!tanPlayer.hasTown()) {
                TanChatUtils.message(
                    player, Lang.PLAYER_NO_TOWN.get(tanPlayer), SoundEnum.NOT_ALLOWED);
                return;
              }

              TanChatUtils.message(
                  player, Lang.PLAYER_RIGHT_CLICK_2_POINTS_TO_CREATE_PROPERTY.get(tanPlayer));
              RightClickListener.register(player, new CreatePlayerPropertyEvent(player));
              player.closeInventory();
            })
        .asGuiItem(player, langType);
  }

  private List<GuiItem> getProperties() {
    List<GuiItem> guiItems = new ArrayList<>();
    for (PropertyData propertyData : tanPlayer.getProperties()) {

      guiItems.add(
          iconManager
              .get(propertyData.getIcon())
              .setName(propertyData.getName())
              .setDescription(propertyData.getBasicDescription())
              .setAction(event -> PlayerPropertyManager.open(player, propertyData, p -> open()))
              .asGuiItem(player, langType));
    }
    return guiItems;
  }
}
