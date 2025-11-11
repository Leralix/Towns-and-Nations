package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class PlayerPropertyManager extends PropertyMenus {

  private final Consumer<Player> onClose;

  private PlayerPropertyManager(
      Player player, ITanPlayer tanPlayer, PropertyData propertyData, Consumer<Player> onClose) {
    super(
        player,
        tanPlayer,
        Lang.HEADER_PLAYER_SPECIFIC_PROPERTY.get(tanPlayer.getLang(), propertyData.getName()),
        3,
        propertyData);
    this.onClose = onClose;
  }

  public static void open(Player player, PropertyData propertyData, Consumer<Player> onClose) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new PlayerPropertyManager(player, tanPlayer, propertyData, onClose).open();
            });
  }

  @Override
  public void open() {
    gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BROWN_STAINED_GLASS_PANE));

    gui.setItem(1, 5, getPropertyIcon());

    gui.setItem(2, 2, getRenameButton());
    gui.setItem(2, 3, getDescriptionButton());
    gui.setItem(2, 4, getAuthorizedPlayersButton());
    gui.setItem(2, 5, getBoundariesButton());
    gui.setItem(2, 6, forRentButton());
    if (propertyData.isRented()) {
      gui.setItem(2, 7, getKickRenterButton());
    } else {
      gui.setItem(2, 7, forSaleButton());
    }
    gui.setItem(2, 8, getDeleteButton());

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, onClose));

    gui.open(player);
  }

  @Override
  protected GuiItem getPropertyIcon() {
    var desc = propertyData.getBasicDescription();
    desc.add(Lang.GUI_PROPERTY_CHANGE_ICON.get());

    return iconManager
        .get(propertyData.getIcon())
        .setName(propertyData.getName())
        .setDescription(desc)
        .setAction(
            event -> {
              if (event.getCursor() == null) {
                return;
              }
              if (event.getCursor().getType() == Material.AIR) {
                return;
              }
              ItemStack itemMaterial = event.getCursor();
              propertyData.setIcon(new CustomIcon(itemMaterial));
              SoundUtil.playSound(player, SoundEnum.GOOD);
              open();
            })
        .asGuiItem(player, langType);
  }
}
