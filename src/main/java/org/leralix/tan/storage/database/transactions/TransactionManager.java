package org.leralix.tan.storage.database.transactions;

import org.leralix.tan.TownsAndNations;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Logger;

public class TransactionManager {

    private final DataSource dataSource;
    private static TransactionManager instance;
    private final Logger pluginLogger;

    private TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.pluginLogger = TownsAndNations.getPlugin().getLogger();
        registerDatabases();
    }

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager(TownsAndNations.getPlugin().getDatabaseHandler().getDataSource());
        }
        return instance;
    }

    private void registerDatabases() {
        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement()) {

            String dbName = conn.getMetaData().getDatabaseProductName().toLowerCase();
            String autoIncrementSyntax;

            if (dbName.contains("sqlite")) {
                autoIncrementSyntax = "INTEGER PRIMARY KEY AUTOINCREMENT";
            } else if (dbName.contains("mysql")) {
                autoIncrementSyntax = "BIGINT AUTO_INCREMENT PRIMARY KEY";
            } else {
                autoIncrementSyntax = "BIGSERIAL PRIMARY KEY";
            }

            for(TransactionType transactionType : TransactionType.values()){
                statement.addBatch(transactionType.getCreateTableSQL().formatted(autoIncrementSyntax));
            }

            statement.executeBatch();

            pluginLogger.info("[TAN] All transaction tables created or verified successfully for DB: " + dbName);

        } catch (SQLException e) {
            pluginLogger.severe("Error while creating transaction tables: " + e.getMessage());
        }
    }

    public List<AbstractTransaction> getTransactionOf(String concernedID, TransactionType transactionType) {
        try (Connection conn = dataSource.getConnection()) {
            return getTransactionOf(conn, concernedID, transactionType);
        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while connecting to the database: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<AbstractTransaction> getTransactionOf(Connection conn, String concernedID, TransactionType wantedRelationType) {
        List<AbstractTransaction> transactions = new ArrayList<>();

        String sqlIndex = """
        SELECT id_transaction, type
        FROM transaction_index
        WHERE concerned = ?
        ORDER BY id_transaction DESC
    """;

        EnumMap<TransactionType, List<Long>> transactionIdsByType = new EnumMap<>(TransactionType.class);

        try (var ps = conn.prepareStatement(sqlIndex)) {
            ps.setString(1, concernedID);

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    long transactionId = rs.getLong("id_transaction");
                    String typeStr = rs.getString("type");

                    try {
                        TransactionType transactionType = TransactionType.valueOf(typeStr);

                        //If relationType is INDEX, show all.

                        if (wantedRelationType != TransactionType.INDEX && transactionType != wantedRelationType) {
                            continue;
                        }

                        transactionIdsByType
                                .computeIfAbsent(transactionType, k -> new ArrayList<>())
                                .add(transactionId);

                    } catch (IllegalArgumentException ignored) {
                        pluginLogger.warning("[TAN] Unknown transaction type in DB: " + typeStr);
                    }
                }
            }
        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while reading index: " + e.getMessage());
            return transactions;
        }

        for (var entry : transactionIdsByType.entrySet()) {
            TransactionType type = entry.getKey();
            List<Long> ids = entry.getValue();

            switch (type) {
                case DONATION -> transactions.addAll(getDonationTransactions(conn, ids));
                case PAYMENT -> transactions.addAll(getPaymentTransactions(conn, ids));
                default -> pluginLogger.warning("[TAN] Unhandled transaction type: " + type);
            }
        }

        return transactions;
    }

    private List<PaymentTransaction> getPaymentTransactions(Connection conn, List<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyList();

        // ⚡ Construction dynamique du WHERE id IN (...)
        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT sender_id, receiver_id, amount, timestamp FROM transaction_payment WHERE id IN (" + placeholders + ")";

        List<PaymentTransaction> donations = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, ids.get(i));
            }

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender_id");
                    String receiver = rs.getString("receiver_id");
                    double amount = rs.getDouble("amount");
                    long timestamp = rs.getLong("timestamp");
                    Instant instant = Instant.ofEpochMilli(timestamp);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


                    donations.add(new PaymentTransaction(dateTime, sender,  receiver, amount));
                }
            }
        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while fetching donation transactions: " + e.getMessage());
        }

        return donations;
    }

    private List<DonationTransaction> getDonationTransactions(Connection conn, List<Long> ids) {
        if (ids.isEmpty()) return Collections.emptyList();

        // ⚡ Construction dynamique du WHERE id IN (...)
        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(java.util.stream.Collectors.joining(","));
        String sql = "SELECT sender_id, receiver_id, amount, timestamp FROM transaction_donation WHERE id IN (" + placeholders + ")";

        List<DonationTransaction> donations = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) {
                ps.setLong(i + 1, ids.get(i));
            }

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender_id");
                    String receiver = rs.getString("receiver_id");
                    double amount = rs.getDouble("amount");
                    long timestamp = rs.getLong("timestamp");
                    Instant instant = Instant.ofEpochMilli(timestamp);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());


                    donations.add(new DonationTransaction(dateTime, sender,  receiver, amount));
                }
            }
        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while fetching donation transactions: " + e.getMessage());
        }

        return donations;
    }


    public void register(AbstractTransaction transaction) {
        try (Connection conn = dataSource.getConnection()) {
            switch (transaction) {
                case DonationTransaction donationTransaction -> insertDonation(conn, donationTransaction);
                case PaymentTransaction paymentTransaction -> insertPaymentTransaction(conn, paymentTransaction);
                default -> pluginLogger.warning("[TAN] Unknown transaction type: " + transaction.getType());
            }
        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while inserting transaction: " + e.getMessage());
        }
    }

    private void insertPaymentTransaction(Connection conn, PaymentTransaction paymentTransaction) throws SQLException {
        long donationId;

        String sqlDonation = "INSERT INTO transaction_payment (timestamp, sender_id, receiver_id, amount) VALUES (?, ?, ?, ?)";

        try (var ps = conn.prepareStatement(sqlDonation, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, paymentTransaction.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            ps.setString(2, paymentTransaction.getSenderID());
            ps.setString(3, paymentTransaction.getReceiverID());
            ps.setDouble(4, paymentTransaction.getAmount());
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    donationId = rs.getLong(1);
                } else {
                    throw new SQLException("Failed to retrieve generated donation ID");
                }
            }
        }

        String sqlIndex = "INSERT INTO transaction_index (concerned, id_transaction, type) VALUES (?, ?, ?)";
        try (var ps = conn.prepareStatement(sqlIndex)) {
            ps.setString(1, paymentTransaction.getSenderID());
            ps.setLong(2, donationId);
            ps.setString(3, TransactionType.PAYMENT.name());
            ps.executeUpdate();
        }

        try (var ps = conn.prepareStatement(sqlIndex)) {
            ps.setString(1, paymentTransaction.getReceiverID());
            ps.setLong(2, donationId);
            ps.setString(3, TransactionType.PAYMENT.name());
            ps.executeUpdate();
        }
    }

    private void insertDonation(Connection conn, DonationTransaction tx) throws SQLException {
        long donationId;

        String sqlDonation = "INSERT INTO transaction_donation (timestamp, sender_id, receiver_id, amount) VALUES (?, ?, ?, ?)";

        try (var ps = conn.prepareStatement(sqlDonation, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, tx.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            ps.setString(2, tx.getPlayerID());
            ps.setString(3, tx.getTerritoryID());
            ps.setDouble(4, tx.getAmount());
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    donationId = rs.getLong(1);
                } else {
                    throw new SQLException("Failed to retrieve generated donation ID");
                }
            }
        }

        String sqlIndex = "INSERT INTO transaction_index (concerned, id_transaction, type) VALUES (?, ?, ?)";
        try (var ps = conn.prepareStatement(sqlIndex)) {
            ps.setString(1, tx.getTerritoryID());
            ps.setLong(2, donationId);
            ps.setString(3, TransactionType.DONATION.name());
            ps.executeUpdate();
        }

        try (var ps = conn.prepareStatement(sqlIndex)) {
            ps.setString(1, tx.getPlayerID());
            ps.setLong(2, donationId);
            ps.setString(3, TransactionType.DONATION.name());
            ps.executeUpdate();
        }
    }
}
