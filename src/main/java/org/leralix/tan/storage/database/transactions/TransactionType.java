package org.leralix.tan.storage.database.transactions;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum TransactionType {

    //%s because each DB acts differently for auto id
    INDEX(Lang.ALL_TRANSACTION_SCOPE, "transaction_index",
    """
            id %s,
            id_transaction BIGINT NOT NULL,
            concerned VARCHAR(32) NOT NULL,
            type VARCHAR(32) NOT NULL
        );
    """),
    PAYMENT(Lang.PAYMENT_TRANSACTION_SCOPE, "transaction_payment",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            sender_id BIGINT NOT NULL,
            receiver_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    DONATION(Lang.DONATION_TRANSACTION_SCOPE, "transaction_donation",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            player_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    RETRIEVE(Lang.RETRIEVE_TRANSACTION_SCOPE, "transaction_retrieve",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            player_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    TAXES(Lang.TAXES_TRANSACTION_SCOPE, "transaction_taxes",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            player_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    SALARY(Lang.SALARY_TRANSACTION_SCOPE, "transaction_salary",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            player_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    CREATE_PROPERTY(Lang.CREATE_PROPERTY_TRANSACTION_SCOPE, "transaction_create_property",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            property_id VARCHAR(32) NOT NULL,
            territory_id BIGINT NOT NULL,
            player_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL,
            tax_per_block DOUBLE PRECISION NOT NULL
        );
    """),
    SELLING_PROPERTY(Lang.SELL_PROPERTY_TRANSACTION_SCOPE, "transaction_selling_property",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            property_id VARCHAR(32) NOT NULL,
            territory_id BIGINT NOT NULL,
            seller_id BIGINT NOT NULL,
            buyer_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL,
            tax_percentage DOUBLE PRECISION NOT NULL
        );
    """),
    RENTING_PROPERTY(Lang.RENT_PROPERTY_TRANSACTION_SCOPE, "transaction_renting_property",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            property_id VARCHAR(32) NOT NULL,
            player_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL,
            tax_percentage DOUBLE PRECISION NOT NULL
        );
    """),
    CHANGE_TERRITORY_NAME(Lang.CHANGE_TERRITORY_NAME_TRANSACTION_SCOPE, "transaction_change_territory_name",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            amount DOUBLE PRECISION NOT NULL,
            tax_percentage DOUBLE PRECISION NOT NULL
        );
    """),
    UPGRADE(Lang.UPGRADE_TRANSACTION_SCOPE, "transaction_upgrade",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            upgrade_id BIGINT NOT NULL,
            upgrade_new_level INT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    TERRITORY_TAX(Lang.TAXES_TRANSACTION_SCOPE, "transaction_territory_taxes",
            """
                id %s,
                timestamp TIMESTAMP NOT NULL,
                sender_id BIGINT NOT NULL,
                receiver_id BIGINT NOT NULL,
                amount DOUBLE PRECISION NOT NULL
            );
        """)
    ;


    private final Lang name;
    private final String tableName;
    private final String createTableSQL;

    TransactionType(Lang name, String tableName, String createTableSQL) {
        this.name = name;
        this.tableName = tableName;
        this.createTableSQL = createTableSQL;
    }

    public String getCreateTableSQL(String autoIncrementSyntax) {
        String title = "CREATE TABLE IF NOT EXISTS %s (".formatted(tableName);
        return title + createTableSQL.formatted(autoIncrementSyntax);
    }


    public TransactionType next(EntityScope scope){
        return switch (this){
            case INDEX -> TransactionType.PAYMENT;
            case PAYMENT -> TransactionType.DONATION;
            case DONATION -> TransactionType.RETRIEVE;
            case RETRIEVE -> TransactionType.TAXES;
            case TAXES -> TransactionType.SALARY;
            case SALARY -> TransactionType.CREATE_PROPERTY;
            case CREATE_PROPERTY -> TransactionType.SELLING_PROPERTY;
            case SELLING_PROPERTY -> TransactionType.RENTING_PROPERTY;
            case RENTING_PROPERTY -> TransactionType.CHANGE_TERRITORY_NAME;
            case CHANGE_TERRITORY_NAME -> {
                if(scope == EntityScope.TERRITORY){
                    yield TransactionType.UPGRADE;
                } 
                else {
                    yield TransactionType.INDEX;
                }
            }
            case UPGRADE -> TransactionType.TERRITORY_TAX;
            case TERRITORY_TAX -> TransactionType.INDEX;
        };
    }

    public String getName(LangType langType) {
        return name.get(langType);
    }

    public String getTableName() {
        return tableName;
    }

}
