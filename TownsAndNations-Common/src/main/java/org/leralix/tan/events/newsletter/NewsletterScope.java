package org.leralix.tan.events.newsletter;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.DisplayableEnum;

public enum NewsletterScope implements DisplayableEnum {
    SHOW_ALL(Lang.NEWSLETTER_SHOW_ALL),
    SHOW_ONLY_UNREAD(Lang.NEWSLETTER_SHOW_ONLY_UNREAD);

    private final Lang name;
    private NewsletterScope nextScope;

    NewsletterScope(Lang name) {
        this.name = name;
    }

    static {
        SHOW_ALL.nextScope = SHOW_ONLY_UNREAD;
        SHOW_ONLY_UNREAD.nextScope = SHOW_ALL;
    }

    public String getDisplayName(LangType langType) {
        return name.get(langType);
    }

    public NewsletterScope getNextScope() {
        return nextScope;
    }
}
