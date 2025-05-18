package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.Newsletter;

import java.sql.SQLException;
import java.util.UUID;

public interface NewsletterSubDAO<T extends Newsletter> {

    void save(T newsletter) throws SQLException;

    T load(UUID id, long date) throws SQLException;
}
