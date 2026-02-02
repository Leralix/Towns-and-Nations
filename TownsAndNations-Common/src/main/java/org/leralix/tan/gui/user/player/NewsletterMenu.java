package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.events.newsletter.NewsletterScope;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;

import java.util.List;

public class NewsletterMenu extends IteratorGUI {

    private NewsletterScope scope;

    public NewsletterMenu(Player player) {
        super(player, Lang.HEADER_NEWSLETTER, 6);
        this.scope = NewsletterScope.SHOW_ONLY_UNREAD;
    }

    @Override
    public void open() {

        GuiUtil.createIterator(gui, getNewsletters(), page, player,
                p -> new PlayerMenu(player),
                p -> nextPage(),
                p -> previousPage()
        );

        gui.setItem(6,4, getMarkAllAsReadButton());
        gui.setItem(6,5, getCheckScopeGui());

        gui.open(player);
    }

    private List<GuiItem> getNewsletters() {
        return NewsletterStorage.getInstance().getNewsletterForPlayer(player, scope, p -> open(), langType);
    }

    private GuiItem getMarkAllAsReadButton() {
        return IconManager.getInstance().get(IconKey.MARK_ALL_AS_READ_ICON)
                .setName(Lang.MARK_ALL_AS_READ.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(event -> {
                    NewsletterStorage.getInstance().markAllAsReadForPlayer(player);
                    open();
                })
                .asGuiItem(player, langType);
    }

    private GuiItem getCheckScopeGui() {
        return GuiUtil.getNextScopeButton(
                iconManager,
                this,
                scope,
                nextScope -> scope = nextScope,
                langType,
                player
        );
    }


    public BasicGui setScope(NewsletterScope newsletterScope) {
        this.scope = newsletterScope;
        return this;
    }
}
