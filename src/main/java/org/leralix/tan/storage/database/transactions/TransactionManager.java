package org.leralix.tan.storage.database.transactions;

import org.jetbrains.annotations.NotNull;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.database.transactions.instance.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Logger;

public class TransactionManager {

    private final DataSource dataSource;
    private static TransactionManager instance;
    private final Logger pluginLogger;
    private final String SQL_INDEX_STATEMENT = "INSERT INTO transaction_index (concerned, id_transaction, type) VALUES (?, ?, ?)";

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
                statement.addBatch(transactionType.getCreateTableSQL(autoIncrementSyntax));
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

    private List<AbstractTransaction> getTransactionOf(Connection conn, String concernedID, TransactionType wantedType) {

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

                        if (wantedType != TransactionType.INDEX && transactionType != wantedType) continue;

                        transactionIdsByType
                                .computeIfAbsent(transactionType, k -> new ArrayList<>())
                                .add(transactionId);

                    } catch (IllegalArgumentException ignored) {
                        pluginLogger.warning("[TAN] Unknown transaction type in DB: " + typeStr);
                    }
                }
            }
        }
        catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while reading index: " + e.getMessage());
            return Collections.emptyList();
        }

        return getTransactionById(conn, transactionIdsByType);
    }

    private @NotNull List<AbstractTransaction> getTransactionById(
            Connection conn,
            EnumMap<TransactionType, List<Long>> transactionIdsByType
    ) {
        List<AbstractTransaction> transactions = new ArrayList<>();

        for (var entry : transactionIdsByType.entrySet()) {
            TransactionType type = entry.getKey();
            List<Long> ids = entry.getValue();


            var wantedClass = switch (type) {
                case PAYMENT -> PaymentTransaction.class;
                case DONATION -> DonationTransaction.class;
                case RETRIEVE -> RetrieveTransaction.class;
                case TAXES -> PlayerTaxTransaction.class;
                case TERRITORY_TAX -> TerritoryTaxTransaction.class;
                case SALARY -> SalaryTransaction.class;
                case UPGRADE -> UpgradeTransaction.class;
                case CREATE_PROPERTY -> CreatingPropertyTransaction.class;
                case SELLING_PROPERTY -> SellingPropertyTransaction.class;
                case RENTING_PROPERTY -> RentingPropertyTransaction.class;
                case TERRITORY_CHUNK_UPKEEP -> TerritoryChunkUpkeepTransaction.class;
                default -> null;
            };
            if(wantedClass == null){
                pluginLogger.warning("[TAN] Unhandled transaction type: " + type);
                continue;
            }

            transactions.addAll(getTransactionsGeneric(conn, ids, type, wantedClass));
        }

        return transactions;
    }

    /**
     * Generic transaction fetcher that can return a typed list (PaymentTransaction, DonationTransaction, etc.)
     */
    private <T extends AbstractTransaction> List<T> getTransactionsGeneric(
            Connection conn,
            List<Long> ids,
            TransactionType transactionType,
            Class<T> clazz
    ) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT * FROM " + transactionType.getTableName() + " WHERE id IN (" + placeholders + ")";

        List<T> results = new ArrayList<>();

        try (var ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < ids.size(); i++) ps.setLong(i + 1, ids.get(i));

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(AbstractTransaction.fromResultSet(rs, clazz));
                }
            }

        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while fetching on " + transactionType.getTableName() + ": " + e.getMessage());
        }

        return results;
    }

    public void register(AbstractTransaction transaction) {
        try (Connection conn = dataSource.getConnection()) {
            insertTransaction(conn, transaction);
        } catch (SQLException e) {
            pluginLogger.severe("[TAN] Error while inserting transaction: " + e.getMessage());
        }
    }

    private void insertTransaction(Connection conn, AbstractTransaction transaction) throws SQLException {
        long transactionID;

        try (var ps = conn.prepareStatement(transaction.getInsertSQL(), Statement.RETURN_GENERATED_KEYS)) {
            transaction.fillInsertStatement(ps);
            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    transactionID = rs.getLong(1);
                } else {
                    throw new SQLException("Failed to retrieve generated transaction ID for " + transaction.getType());
                }
            }
        }

        addToIndex(conn, transactionID, transaction.getType(), transaction.getConcerned());
    }

    private void addToIndex(Connection conn, long transactionId, TransactionType transactionType, Set<String> concerned) throws SQLException {
        if (concerned == null || concerned.isEmpty()) {
            return;
        }

        try (var ps = conn.prepareStatement(SQL_INDEX_STATEMENT)) {
            for (String c : concerned) {
                ps.setString(1, c);
                ps.setLong(2, transactionId);
                ps.setString(3, transactionType.name());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

}
