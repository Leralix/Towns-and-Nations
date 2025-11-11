package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.gui.user.property.PlayerPropertiesMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class PlayerMenu extends BasicGui {

  private PlayerMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_PLAYER_PROFILE.get(tanPlayer.getLang()), 3);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new PlayerMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {

    gui.setItem(1, 5, getPlayerHeadIcon());
    gui.setItem(2, 2, getBalanceButton());
    gui.setItem(2, 3, getPropertyButton());
    gui.setItem(2, 4, getNewsletterButton());
    gui.setItem(2, 6, getTimezoneButton());
    gui.setItem(2, 8, getLanguageButton());

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, MainMenu::open));

    gui.open(player);
  }

  private GuiItem getPlayerHeadIcon() {
    return IconManager.getInstance()
        .get(IconKey.PLAYER_HEAD_ICON)
        .setName(Lang.GUI_PLAYER_ICON.get(tanPlayer, player.getName()))
        .asGuiItem(player, langType);
  }

  private GuiItem getBalanceButton() {
    return IconManager.getInstance()
        .get(IconKey.PLAYER_BALANCE_ICON)
        .setName(Lang.GUI_YOUR_BALANCE.get(langType, player.getName()))
        .setDescription(
            Lang.GUI_YOUR_BALANCE_DESC1.get(Double.toString(EconomyUtil.getBalance(player))))
        .asGuiItem(player, langType);
  }

  private GuiItem getPropertyButton() {
    return IconManager.getInstance()
        .get(IconKey.PLAYER_PROPERTY_ICON)
        .setName(Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(langType))
        .setDescription(Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get())
        .setAction(event -> PlayerPropertiesMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getNewsletterButton() {
    return IconManager.getInstance()
        .get(IconKey.NEWSLETTER_ICON)
        .setName(Lang.GUI_PLAYER_NEWSLETTER.get(tanPlayer))
        .setDescription(Lang.GUI_PLAYER_NEWSLETTER_DESC1.get("?")) // Count loaded async
        .setAction(event -> NewsletterMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getTimezoneButton() {
    TimeZoneManager timeZoneManager = TimeZoneManager.getInstance();
    return iconManager
        .get(IconKey.TIMEZONE_BUTTON)
        .setName(Lang.GUI_TIMEZONE_BUTTON.get(tanPlayer))
        .setDescription(
            Lang.GUI_TIMEZONE_BUTTON_SERVER_ZONE.get(
                timeZoneManager.getTimezoneEnum().getName(tanPlayer.getLang())),
            Lang.GUI_TIMEZONE_BUTTON_PLAYER_ZONE.get(
                tanPlayer.getTimeZone().getName(tanPlayer.getLang())))
        .setAction(p -> PlayerSelectTimezoneMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getLanguageButton() {

    LangType serverLang = Lang.getServerLang();
    LangType playerLang = tanPlayer.getLang();

    return IconManager.getInstance()
        .get(IconKey.LANGUAGE_ICON)
        .setName(Lang.GUI_LANGUAGE_BUTTON.get(tanPlayer))
        .setDescription(
            Lang.GUI_LANGUAGE_BUTTON_DESC1.get(serverLang.getName()),
            Lang.GUI_LANGUAGE_BUTTON_DESC2.get(playerLang.getName()))
        .setAction(event -> LangMenu.open(player))
        .asGuiItem(player, langType);
  }
}
