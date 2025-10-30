package org.leralix.tan.storage.database.transactions;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.scope.EntityScope;

public enum TransactionType {

    //%s because each DB acts differently for auto id
    INDEX(Lang.ALL_TRANSACTION_SCOPE,
    """
        CREATE TABLE IF NOT EXISTS transaction_index (
            id %s,
            id_transaction BIGINT NOT NULL,
            concerned VARCHAR(32) NOT NULL,
            type VARCHAR(32) NOT NULL
        );
    """),
    PAYMENT(Lang.PAYMENT_TRANSACTION_SCOPE, """
        CREATE TABLE IF NOT EXISTS transaction_payment (
            id %s,
            timestamp TIMESTAMP NOT NULL,
            sender_id BIGINT NOT NULL,
            receiver_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    DONATION(Lang.DONATION_TRANSACTION_SCOPE, """
        CREATE TABLE IF NOT EXISTS transaction_donation (
            id %s,
            timestamp TIMESTAMP NOT NULL,
            sender_id BIGINT NOT NULL,
            receiver_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    UPGRADE(Lang.UPGRADE_TRANSACTION_SCOPE, """
        CREATE TABLE IF NOT EXISTS transaction_upgrade (
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            upgrade_id BIGINT NOT NULL,
            upgrade_new_level INT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """);

    private final Lang name;
    private final String createTableSQL;

    TransactionType(Lang name, String createTableSQL) {
        this.name = name;
        this.createTableSQL = createTableSQL;
    }

    public String getCreateTableSQL() {
        return createTableSQL;
    }


    public TransactionType next(EntityScope scope){
        return switch (this){
            case INDEX -> {
                if(scope == EntityScope.TERRITORY){
                    yield TransactionType.DONATION;
                }
                else {
                    yield TransactionType.PAYMENT;
                }
            }
            case PAYMENT -> TransactionType.DONATION;
            case DONATION -> {
                if(scope == EntityScope.TERRITORY){
                    yield TransactionType.UPGRADE;
                }
                else {
                    yield TransactionType.INDEX;
                }
            }
            case UPGRADE -> TransactionType.INDEX;
        };
    }

    public String getName(LangType langType) {
        return name.get(langType);
    }
}
