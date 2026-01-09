package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.DateUtil;

import java.util.function.Consumer;

public final class NewsletterGuiItemUtil {

    private NewsletterGuiItemUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static GuiItem createMarkAsReadGuiItem(
            Player player,
            LangType lang,
            long date,
            IconKey iconKey,
            String title,
            FilledLang message,
            Consumer<Player> markAsRead,
            Consumer<Player> onClick
    ) {
        return IconManager.getInstance().get(iconKey)
                .setName(title)
                .setDescription(
                        Lang.NEWSLETTER_DATE.get(DateUtil.getRelativeTimeDescription(lang, date)),
                        message,
                        Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    if (action.isRightClick()) {
                        markAsRead.accept(player);
                        onClick.accept(player);
                    }
                })
                .asGuiItem(player, lang);
    }
}
