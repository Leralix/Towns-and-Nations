package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneEnum;

public class PlayerSelectTimezoneMenu extends IteratorGUI {

  private PlayerSelectTimezoneMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_SELECT_TIMEZONE.get(tanPlayer.getLang()), 4);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new PlayerSelectTimezoneMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    iterator(getTimezones(), PlayerMenu::open);

    gui.open(player);
  }

  private List<GuiItem> getTimezones() {
    List<GuiItem> timezones = new ArrayList<>();

    for (TimeZoneEnum timeZoneEnum : TimeZoneEnum.values()) {
      timezones.add(
          iconManager
              .get(IconKey.TIMEZONE_BUTTON)
              .setName(timeZoneEnum.getName(tanPlayer.getLang()))
              .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
              .setAction(
                  action -> {
                    tanPlayer.setTimeZone(timeZoneEnum);
                    PlayerMenu.open(player);
                  })
              .asGuiItem(player, langType));
    }
    return timezones;
  }
}
