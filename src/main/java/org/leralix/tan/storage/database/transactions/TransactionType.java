package org.leralix.tan.storage.database.transactions;

import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.DisplayableEnum;

public enum TransactionType implements DisplayableEnum {

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
            amount DOUBLE PRECISION NOT NULL,
            enough_money SMALLINT NOT NULL CHECK (enough_money IN (0, 1))
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
    UPGRADE(Lang.UPGRADE_TRANSACTION_SCOPE, "transaction_upgrades",
        """
            id %s,
            timestamp TIMESTAMP NOT NULL,
            territory_id BIGINT NOT NULL,
            upgrade_id BIGINT NOT NULL,
            upgrade_new_level INT NOT NULL,
            amount DOUBLE PRECISION NOT NULL
        );
    """),
    TERRITORY_TAX(Lang.TERRITORY_TAXES_TRANSACTION_SCOPE, "transaction_territory_taxes",
            """
                id %s,
                timestamp TIMESTAMP NOT NULL,
                sender_id BIGINT NOT NULL,
                receiver_id BIGINT NOT NULL,
                amount DOUBLE PRECISION NOT NULL,
                enough_money SMALLINT NOT NULL CHECK (enough_money IN (0, 1))
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

    @Override
    public String getDisplayName(LangType langType) {
        return name.get(langType);
    }

    public String getTableName() {
        return tableName;
    }

    public TransactionType previous(EntityScope scope) {
        return switch (this) {
            case INDEX -> {
                if (scope == EntityScope.TERRITORY) {
                    yield TransactionType.TERRITORY_TAX;
                } else {
                    yield TransactionType.CHANGE_TERRITORY_NAME;
                }
            }
            case PAYMENT -> TransactionType.INDEX;
            case DONATION -> TransactionType.PAYMENT;
            case RETRIEVE -> TransactionType.DONATION;
            case TAXES -> TransactionType.RETRIEVE;
            case SALARY -> TransactionType.TAXES;
            case CREATE_PROPERTY -> TransactionType.SALARY;
            case SELLING_PROPERTY -> TransactionType.CREATE_PROPERTY;
            case RENTING_PROPERTY -> TransactionType.SELLING_PROPERTY;
            case CHANGE_TERRITORY_NAME -> TransactionType.RENTING_PROPERTY;
            case UPGRADE -> TransactionType.CHANGE_TERRITORY_NAME;
            case TERRITORY_TAX -> TransactionType.UPGRADE;
        };
    }
}
