package org.leralix.tan.gui.user.player;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.events.newsletter.NewsletterScope;
import org.leralix.tan.events.newsletter.NewsletterStorage;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.FoliaScheduler;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class NewsletterMenu extends IteratorGUI {

  NewsletterScope scope;
  private List<GuiItem> cachedNewsletters = new ArrayList<>();
  private boolean isLoaded = false;

  private NewsletterMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_NEWSLETTER.get(tanPlayer.getLang()), 6);
    this.scope = NewsletterScope.SHOW_ONLY_UNREAD;
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new NewsletterMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    // Show loading screen immediately with current cached data
    GuiUtil.createIterator(
        gui,
        cachedNewsletters,
        page,
        player,
        PlayerMenu::open,
        p -> nextPage(),
        p -> previousPage());

    gui.setItem(6, 4, getMarkAllAsReadButton());
    gui.setItem(6, 5, getCheckScopeGui());

    gui.open(player);

    // Load newsletters asynchronously ONLY if not already loaded
    if (!isLoaded) {
      loadNewslettersAsync();
    }
  }

  private void loadNewslettersAsync() {
    FoliaScheduler.runTaskAsynchronously(
        org.leralix.tan.TownsAndNations.getPlugin(),
        () -> {
          // This runs on async thread - safe to do blocking DB calls
          List<GuiItem> newsletters =
              NewsletterStorage.getInstance().getNewsletterForPlayer(player, scope, p -> refresh());
          cachedNewsletters = newsletters;
          isLoaded = true;

          // Refresh menu on main thread with loaded data
          FoliaScheduler.runTask(
              org.leralix.tan.TownsAndNations.getPlugin(),
              () -> {
                if (gui != null && player.isOnline()) {
                  refresh();
                }
              });
        });
  }

  private void refresh() {
    // Just update the GUI without triggering another load
    GuiUtil.createIterator(
        gui,
        cachedNewsletters,
        page,
        player,
        PlayerMenu::open,
        p -> nextPage(),
        p -> previousPage());
    gui.update();
  }

  private List<GuiItem> getNewsletters() {
    return cachedNewsletters;
  }

  private GuiItem getMarkAllAsReadButton() {
    return IconManager.getInstance()
        .get(IconKey.MARK_ALL_AS_READ_ICON)
        .setName(Lang.MARK_ALL_AS_READ.get(tanPlayer))
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
        .setAction(
            event -> {
              NewsletterStorage.getInstance().markAllAsReadForPlayer(player);
              isLoaded = false; // Force reload
              open();
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getCheckScopeGui() {
    return IconManager.getInstance()
        .get(IconKey.CHANGE_NEWSLETTER_SCOPE_ICON)
        .setName(Lang.NEWSLETTER_SCOPE_ICON.get(tanPlayer))
        .setDescription(Lang.NEWSLETTER_SCOPE_ICON_DESC1.get(scope.getName(tanPlayer.getLang())))
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
        .setAction(
            event -> {
              scope = scope.getNextScope();
              isLoaded = false; // Force reload with new scope
              open();
            })
        .asGuiItem(player, langType);
  }

  public BasicGui setScope(NewsletterScope newsletterScope) {
    this.scope = newsletterScope;
    return this;
  }
}
