package org.leralix.tan.newsletter;

import org.leralix.tan.newsletter.news.Newsletter;

import java.util.ArrayList;
import java.util.Collection;

public class NewsletterCategory {

    Collection<Newsletter> newsletters;

    public NewsletterCategory() {
        this.newsletters = new ArrayList<>();
    }

    public void add(Newsletter newsletter) {
        newsletters.add(newsletter);
    }

    public Collection<Newsletter> getAll() {
        return newsletters;
    }
}
