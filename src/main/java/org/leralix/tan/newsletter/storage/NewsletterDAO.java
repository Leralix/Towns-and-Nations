package org.leralix.tan.newsletter.storage;

import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.newsletter.news.Newsletter;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class NewsletterDAO {
    private final Map<NewsletterType, NewsletterSubDAO<?>> subDaos;

    private final Connection connection;

    public NewsletterDAO(Connection connection) {
        this.subDaos = new EnumMap<>(NewsletterType.class);
        this.connection = connection;

        createTableIfNotExists();

        subDaos.put(NewsletterType.TOWN_CREATED, new PlayerCreateTownDAO(connection));
        subDaos.put(NewsletterType.TOWN_DELETED, new PlayerDeleteTownDAO(connection));
        subDaos.put(NewsletterType.PLAYER_APPLICATION, new PlayerApplicationDAO(connection));
        subDaos.put(NewsletterType.PLAYER_JOIN_TOWN, new PlayerJoinTownDAO(connection));
        subDaos.put(NewsletterType.REGION_CREATED, new PlayerCreateRegionDAO(connection));
        subDaos.put(NewsletterType.REGION_DELETED, new PlayerDeleteRegionDAO(connection));

        subDaos.put(NewsletterType.DIPLOMACY_ACCEPTED, new DiplomacyAcceptedDAO(connection));
        subDaos.put(NewsletterType.DIPLOMACY_PROPOSAL, new DiplomacyProposalDAO(connection));
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS newsletter (" +
                "id UUID PRIMARY KEY, " +
                "type VARCHAR(255) NOT NULL, " +
                "date_created TIMESTAMP NOT NULL" +
                ")";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create newsletter table", e);
        }

        sql = "CREATE TABLE IF NOT EXISTS newsletter_read (" +
                "newsletter_id UUID NOT NULL, " +
                "player_id UUID NOT NULL, " +
                "PRIMARY KEY (newsletter_id, player_id)" +
                ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create newsletter table", e);
        }


    }

    public void save(Newsletter newsletter) throws SQLException {
        String sql = "INSERT INTO newsletter (id, type, date_created) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, newsletter.getId());
            ps.setString(2, newsletter.getType().name());
            ps.setTimestamp(3,
                    Timestamp.valueOf(
                            LocalDateTime.ofInstant(
                                    Instant.ofEpochSecond(newsletter.getDate()),
                                    TimeZone.getDefault().toZoneId()))
            );
            ps.executeUpdate();
        }


        NewsletterSubDAO subDAO = subDaos.get(newsletter.getType());
        if (subDAO == null){
            throw new IllegalStateException("No DAO for type " + newsletter.getType());
        }
        subDAO.save(newsletter);
    }


    public Newsletter load(UUID id) throws SQLException {
        String sql = "SELECT * FROM newsletter WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                NewsletterType type = NewsletterType.valueOf(rs.getString("type"));
                LocalDateTime dateCreated = rs.getTimestamp("date_created").toLocalDateTime();

                NewsletterSubDAO<?> subDAO = subDaos.get(type);
                if (subDAO == null) throw new IllegalStateException("No DAO for type " + type);

                return subDAO.load(id, dateCreated.toInstant(ZoneOffset.UTC).toEpochMilli());
            }
        }
    }

    public void markAsRead(UUID newsletterId, UUID playerId) {

        try {
            String sql = "INSERT INTO newsletter_read (newsletter_id, player_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setObject(1, newsletterId);
                ps.setObject(2, playerId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create newsletter_read table", e);
        }
    }

    public boolean hasRead(UUID newsletterId, UUID playerId) {
        try {
            String sql = "SELECT 1 FROM newsletter_read WHERE newsletter_id = ? AND player_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setObject(1, newsletterId);
                ps.setObject(2, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to check if newsletter has been read", e);
        }
    }

    public List<Newsletter> getNewsletters() {
        Duration duration = Duration.ofDays(7);
        LocalDateTime cutoff = LocalDateTime.now().minus(duration);
        String sql = "SELECT * FROM newsletter WHERE date_created >= ? ORDER BY date_created DESC";
        List<Newsletter> newsletters = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));
                    LocalDateTime createdAt = rs.getTimestamp("date_created").toLocalDateTime();
                    NewsletterType type = NewsletterType.valueOf(rs.getString("type"));

                    NewsletterSubDAO<?> subDAO = subDaos.get(type);
                    if (subDAO == null) {
                        System.err.println("Aucun DAO pour le type " + type + ", id: " + id);
                        continue;
                    }

                    Newsletter newsletter = subDAO.load(id, createdAt.toInstant(ZoneOffset.UTC).toEpochMilli());
                    if (newsletter != null) {
                        newsletters.add(newsletter);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to get newsletters", e);
        }

        return newsletters;
    }
}
