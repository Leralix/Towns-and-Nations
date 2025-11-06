package org.leralix.tan.gui.user.property;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.territory.TownMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class TownPropertiesMenu extends IteratorGUI {

  private final TownData townData;

  public TownPropertiesMenu(Player player, TownData townData) {
    super(player, Lang.HEADER_PLAYER_PROPERTIES, 3);
    this.townData = townData;
    open();
  }

  @Override
  public void open() {

    GuiUtil.createIterator(
        gui,
        getProperties(),
        page,
        player,
        p -> new TownMenu(player, townData),
        p -> nextPage(),
        p -> previousPage());

    gui.open(player);
  }

  private List<GuiItem> getProperties() {
    List<GuiItem> res = new ArrayList<>();

    for (PropertyData townProperty : townData.getProperties()) {

      res.add(
          iconManager
              .get(townProperty.getIcon())
              .setName(townProperty.getName())
              .setDescription(townProperty.getBasicDescription())
              .setAction(
                  event -> {
                    event.setCancelled(true);
                    if (!tanPlayer.hasTown()) {
                      TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(tanPlayer), NOT_ALLOWED);
                      return;
                    }
                    if (!townData.doesPlayerHavePermission(
                        tanPlayer, RolePermission.MANAGE_PROPERTY)) {
                      TanChatUtils.message(
                          player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                      return;
                    }
                    new TownPropertyManager(player, townProperty, townData);
                  })
              .asGuiItem(player, langType));
    }
    return res;
  }
}
