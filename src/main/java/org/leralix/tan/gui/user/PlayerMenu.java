package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.newsletter.storage.NewsletterStorage;
import org.leralix.tan.utils.GuiUtil;

public class PlayerMenu extends BasicGui {


    public PlayerMenu(Player player) {
        super(player, Lang.HEADER_PLAYER_PROFILE, 3);
    }

    @Override
    public void open(){

        gui.setItem(1, 5, getPlayerHeadIcon());
        gui.setItem(2, 2, getBalanceButton());
        gui.setItem(2, 4, getPropertyButton());
        gui.setItem(2, 6, getNewsletterButton());
        gui.setItem(2, 8, getLanguageButton());

        gui.setItem(3,1, GuiUtil.createBackArrow(player, p -> new MainMenu(p).open()));

        gui.open(player);
    }

    private GuiItem getPlayerHeadIcon() {
        return IconManager.getInstance().get(IconKey.PLAYER_HEAD_ICON)
                .setName(Lang.GUI_PLAYER_ICON.get(playerData, player.getName()))
                .asGuiItem(player);
    }

    private GuiItem getBalanceButton() {
        return IconManager.getInstance().get(IconKey.PLAYER_BALANCE_ICON)
                .setName(Lang.GUI_YOUR_BALANCE.get(playerData, player.getName()))
                .setDescription(Lang.GUI_YOUR_BALANCE_DESC1.get(playerData, EconomyUtil.getBalance(player)))
                .asGuiItem(player);
    }

    private GuiItem getPropertyButton() {
        return IconManager.getInstance().get(IconKey.PLAYER_PROPERTY_ICON)
                .setName(Lang.GUI_PLAYER_MANAGE_PROPERTIES.get(playerData))
                .setDescription(
                        Lang.GUI_PLAYER_MANAGE_PROPERTIES_DESC1.get(playerData),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> new PlayerPropertyMenu(player).open())
                .asGuiItem(player);
    }

    private GuiItem getNewsletterButton() {
        return IconManager.getInstance().get(IconKey.NEWSLETTER_ICON)
                .setName(Lang.GUI_PLAYER_NEWSLETTER.get(playerData))
                .setDescription(
                        Lang.GUI_PLAYER_NEWSLETTER_DESC1.get(playerData, NewsletterStorage.getNbUnreadNewsletterForPlayer(player)),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> new NewsletterMenu(player).open())
                .asGuiItem(player);
    }

    private GuiItem getLanguageButton() {

        LangType serverLang = Lang.getServerLang();
        LangType playerLang = playerData.getLang();

        return IconManager.getInstance().get(IconKey.LANGUAGE_ICON)
                .setName(Lang.GUI_LANGUAGE_BUTTON.get(playerData))
                .setDescription(
                        Lang.GUI_LANGUAGE_BUTTON_DESC1.get(playerData, serverLang.getName()),
                        Lang.GUI_LANGUAGE_BUTTON_DESC2.get(playerData, playerLang.getName()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> new LangMenu(player).open())
                .asGuiItem(player);
    }





}
