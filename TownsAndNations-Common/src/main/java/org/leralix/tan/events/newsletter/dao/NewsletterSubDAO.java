package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.events.newsletter.news.Newsletter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public abstract class NewsletterSubDAO<T extends Newsletter> {

    protected DataSource dataSource;

    protected NewsletterSubDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        createTableIfNotExists();
    }
    protected abstract void createTableIfNotExists();

    public abstract void save(T newsletter, Connection conn) throws SQLException;

    public abstract T load(UUID id, long date, Connection conn) throws SQLException;

    public abstract void delete(UUID uuid);
}
