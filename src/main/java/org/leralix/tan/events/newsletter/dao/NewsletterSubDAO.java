package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.Newsletter;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public abstract class NewsletterSubDAO<T extends Newsletter> {

    protected DataSource dataSource;

    public NewsletterSubDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        createTableIfNotExists();
    }
    protected abstract void createTableIfNotExists();

    public abstract void save(T newsletter) throws SQLException;

    public abstract T load(UUID id, long date) throws SQLException;

    public abstract void delete(UUID uuid);
}
