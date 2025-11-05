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
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.TownsAndNations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NewsletterMenu extends IteratorGUI {

    NewsletterScope scope;

    public NewsletterMenu(Player player) {
        super(player, Lang.HEADER_NEWSLETTER, 6);
        this.scope = NewsletterScope.SHOW_ONLY_UNREAD;
    }

    @Override
    public void open() {
        // Show a loading GUI immediately
        gui.setItem(3, 5, IconManager.getInstance().get(IconKey.LOADING_ICON).asGuiItem(player, tanPlayer.getLang()));
        gui.open(player);

        // Fetch player data and newsletters asynchronously
        PlayerDataStorage.getInstance().get(player).thenCombine(
                NewsletterStorage.getInstance().getNewsletterForPlayer(player, scope, p -> open()),
                (tanPlayer, newsletters) -> {
                    // All GUI updates MUST run on the main thread
                    org.leralix.tan.utils.FoliaScheduler.runTask(TownsAndNations.getPlugin(), () -> {
                        // Clear loading icon
                        gui.removeItem(3, 5);

                        GuiUtil.createIterator(gui, newsletters, page, player,
                                p -> new PlayerMenu(player),
                                p -> nextPage(),
                                p -> previousPage(),
                                tanPlayer // Pass the fetched tanPlayer
                        );

                        gui.setItem(6, 4, getMarkAllAsReadButton(tanPlayer));
                        gui.setItem(6, 5, getCheckScopeGui(tanPlayer));

                        // Re-open the GUI to refresh with loaded content
                        gui.open(player);
                    });
                    return null;
                }
        ).exceptionally(ex -> {
            TownsAndNations.getPlugin().getLogger().severe("Error opening newsletter menu for " + player.getName() + ": " + ex.getMessage());
            // Handle error, perhaps close GUI or show error message
            return null;
        });
    }

    // Helper method to get newsletters (now asynchronous)
    private CompletableFuture<List<GuiItem>> getNewslettersAsync() {
        return NewsletterStorage.getInstance().getNewsletterForPlayer(player, scope, p -> open());
    }

    private GuiItem getMarkAllAsReadButton(ITanPlayer tanPlayer) {
        return IconManager.getInstance().get(IconKey.MARK_ALL_AS_READ_ICON)
                .setName(Lang.MARK_ALL_AS_READ.get(tanPlayer))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
                .setAction(event -> {
                    NewsletterStorage.getInstance().markAllAsReadForPlayer(player);
                    open();
                })
                .asGuiItem(player, tanPlayer.getLang());
    }

    private GuiItem getCheckScopeGui(ITanPlayer tanPlayer) {
        return IconManager.getInstance().get(IconKey.CHANGE_NEWSLETTER_SCOPE_ICON)
                .setName(Lang.NEWSLETTER_SCOPE_ICON.get(tanPlayer))
                .setDescription(Lang.NEWSLETTER_SCOPE_ICON_DESC1.get(scope.getName(tanPlayer.getLang())))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(event -> {
                    scope = scope.getNextScope();
                    open();
                })
                .asGuiItem(player, tanPlayer.getLang());
    }


    public BasicGui setScope(NewsletterScope newsletterScope) {
        this.scope = newsletterScope;
        return this;
    }
}
