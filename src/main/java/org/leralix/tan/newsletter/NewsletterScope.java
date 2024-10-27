package org.leralix.tan.newsletter;

import org.leralix.tan.lang.Lang;

public enum NewsletterScope {
    SHOW_ALL(Lang.NEWSLETTER_SHOW_ALL.get()),
    SHOW_ONLY_UNREAD(Lang.NEWSLETTER_SHOW_ONLY_UNREAD.get());

    private final String name;
    private NewsletterScope nextScope;

    NewsletterScope(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public NewsletterScope getNextScope() {
        return nextScope;
    }

    static {
        SHOW_ALL.nextScope = SHOW_ONLY_UNREAD;
        SHOW_ONLY_UNREAD.nextScope = SHOW_ALL;
    }


}
