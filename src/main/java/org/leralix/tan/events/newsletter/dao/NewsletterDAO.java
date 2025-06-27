package org.leralix.tan.events.newsletter.dao;

import org.leralix.tan.TownsAndNations;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.events.newsletter.news.Newsletter;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class NewsletterDAO {
    private final Map<NewsletterType, NewsletterSubDAO<?>> subDaos;

    private final DataSource dataSource;

    public NewsletterDAO(DataSource dataSource) {
        this.subDaos = new EnumMap<>(NewsletterType.class);
        this.dataSource = dataSource;

        createTableIfNotExists();

        subDaos.put(NewsletterType.TOWN_CREATED, new PlayerCreateTownDAO(dataSource));
        subDaos.put(NewsletterType.TOWN_DELETED, new PlayerDeleteTownDAO(dataSource));
        subDaos.put(NewsletterType.PLAYER_APPLICATION, new PlayerApplicationDAO(dataSource));
        subDaos.put(NewsletterType.PLAYER_JOIN_TOWN, new PlayerJoinTownDAO(dataSource));
        subDaos.put(NewsletterType.REGION_CREATED, new PlayerCreateRegionDAO(dataSource));
        subDaos.put(NewsletterType.REGION_DELETED, new PlayerDeleteRegionDAO(dataSource));

        subDaos.put(NewsletterType.TERRITORY_VASSAL_PROPOSAL, new TerritoryVassalProposalDAO(dataSource));
        subDaos.put(NewsletterType.TERRITORY_VASSAL_ACCEPTED, new TerritoryVassalAcceptedDAO(dataSource));
        subDaos.put(NewsletterType.TERRITORY_VASSAL_FORCED, new TerritoryVassalForcedDAO(dataSource));
        subDaos.put(NewsletterType.TERRITORY_VASSAL_INDEPENDENT, new TerritoryVassalIndependentDAO(dataSource));

        subDaos.put(NewsletterType.DIPLOMACY_ACCEPTED, new DiplomacyAcceptedDAO(dataSource));
        subDaos.put(NewsletterType.DIPLOMACY_PROPOSAL, new DiplomacyProposalDAO(dataSource));

        subDaos.put(NewsletterType.ATTACK_DECLARED, new AttackDeclaredDAO(dataSource));
        subDaos.put(NewsletterType.ATTACK_WON_BY_ATTACKER, new AttackWonByAttackerDAO(dataSource));
        subDaos.put(NewsletterType.ATTACK_WON_BY_DEFENDER, new AttackWonByDefenderDAO(dataSource));
        subDaos.put(NewsletterType.ATTACK_CANCELLED, new AttackCancelledDAO(dataSource));



    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS newsletter (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "type VARCHAR(255) NOT NULL, " +
                "date_created TIMESTAMP NOT NULL" +
                ")";

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create newsletter table", e);
        }

        sql = "CREATE TABLE IF NOT EXISTS newsletter_read (" +
                "newsletter_id VARCHAR(36) NOT NULL, " +
                "player_id VARCHAR(36) NOT NULL, " +
                "PRIMARY KEY (newsletter_id, player_id)" +
                ")";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Failed to create newsletter table", e);
        }


    }

    public void save(Newsletter newsletter) throws SQLException {
        String sql = "INSERT INTO newsletter (id, type, date_created) VALUES (?, ?, ?)";

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
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


    public void markAsRead(UUID newsletterId, UUID playerId) {

        try {
            String sql = "INSERT INTO newsletter_read (newsletter_id, player_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
                ps.setObject(1, newsletterId);
                ps.setObject(2, playerId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add entry in newsletter_read table", e);
        }
    }

    public boolean hasRead(UUID newsletterId, UUID playerId) {
        try {
            String sql = "SELECT 1 FROM newsletter_read WHERE newsletter_id = ? AND player_id = ?";
            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
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

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(cutoff));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));
                    LocalDateTime createdAt = rs.getTimestamp("date_created").toLocalDateTime();

                    String typeName = rs.getString("type");
                    if(!NewsletterType.isValidEnumValue(typeName)) {
                        TownsAndNations.getPlugin().getLogger().severe("Invalid newsletter type: " + typeName);
                        removeNewsletter(id);
                        continue;
                    }

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

    private void removeNewsletter(UUID id) {
        String sql = "DELETE FROM newsletter WHERE id = ?";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove newsletter", e);
        }
    }
}
