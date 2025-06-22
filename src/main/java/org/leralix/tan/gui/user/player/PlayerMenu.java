package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.gui.user.property.PlayerPropertiesMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.newsletter.storage.NewsletterStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.GuiUtil;

public class PlayerMenu extends BasicGui {


    public PlayerMenu(Player player) {
        super(player, Lang.HEADER_PLAYER_PROFILE, 3);
        open();
    }

    @Override
    public void open() {

        gui.setItem(1, 5, getPlayerHeadIcon());
        gui.setItem(2, 2, getBalanceButton());
        gui.setItem(2, 3, getPropertyButton());
        gui.setItem(2, 4, getNewsletterButton());
        gui.setItem(2, 6, getTimezoneButton());
        gui.setItem(2, 8, getLanguageButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, MainMenu::new));

        gui.open(player);
    }

    private GuiItem getPlayerHeadIcon() {
        return IconManager.getInstance().get(IconKey.PLAYER_HEAD_ICON)
                .setName(Lang.GUI_PLAYER_ICON.get(ITanPlayer, player.getName()))
                .asGuiItem(player);
    }

    private GuiItem getBalanceButton() {
        return IconManager.getInstance().get(IconKey.PLAYER_BALANCE_ICON)
                .setName(Lang.GUI_YOUR_BALANCE.get(ITanPlayer, player.getName()))
                .setDescription(Lang.GUI_YOUR_BALANCE_DESC1.get(ITanPlayer, EconomyUtil.getBalance(player)))
                .asGuiItem(player);
    }

    private GuiItem getPropertyButton() {
        return IconManager.getInstance().get(IconKey.PLAYER_PROPERTY_ICON)
                .setName(Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get(ITanPlayer),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer)
                )
                .setAction(event -> new PlayerPropertiesMenu(player).open())
                .asGuiItem(player);
    }

    private GuiItem getNewsletterButton() {
        return IconManager.getInstance().get(IconKey.NEWSLETTER_ICON)
                .setName(Lang.GUI_PLAYER_NEWSLETTER.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_PLAYER_NEWSLETTER_DESC1.get(ITanPlayer, NewsletterStorage.getNbUnreadNewsletterForPlayer(player)),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer)
                )
                .setAction(event -> new NewsletterMenu(player).open())
                .asGuiItem(player);
    }

    private GuiItem getTimezoneButton() {
        TimeZoneManager timeZoneManager = TimeZoneManager.getInstance();
        return iconManager.get(IconKey.TIMEZONE_BUTTON)
                .setName(Lang.GUI_TIMEZONE_BUTTON.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_TIMEZONE_BUTTON_SERVER_ZONE.get(ITanPlayer,
                                timeZoneManager.getServerTimezone().getName(ITanPlayer.getLang())),
                        Lang.GUI_TIMEZONE_BUTTON_PLAYER_ZONE.get(ITanPlayer,
                                ITanPlayer.getTimeZone().getName(ITanPlayer.getLang())),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer)
                )
                .setAction(p -> new PlayerSelectTimezoneMenu(player))
                .asGuiItem(player);
    }

    private GuiItem getLanguageButton() {

        LangType serverLang = Lang.getServerLang();
        LangType playerLang = ITanPlayer.getLang();

        return IconManager.getInstance().get(IconKey.LANGUAGE_ICON)
                .setName(Lang.GUI_LANGUAGE_BUTTON.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_LANGUAGE_BUTTON_DESC1.get(ITanPlayer, serverLang.getName()),
                        Lang.GUI_LANGUAGE_BUTTON_DESC2.get(ITanPlayer, playerLang.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(ITanPlayer)
                )
                .setAction(event -> new LangMenu(player).open())
                .asGuiItem(player);
    }


}
