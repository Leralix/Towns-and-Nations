package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.news.Newsletter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public abstract class NewsletterSubDAO<T extends Newsletter> {

    protected Connection connection;

    public NewsletterSubDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }
    protected abstract void createTableIfNotExists();

    public abstract void save(T newsletter) throws SQLException;

    public abstract T load(UUID id, long date) throws SQLException;
}
