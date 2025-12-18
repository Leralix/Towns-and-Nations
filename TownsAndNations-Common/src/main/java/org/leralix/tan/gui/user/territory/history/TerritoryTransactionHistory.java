package org.leralix.tan.gui.user.territory.history;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.EntityScope;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.storage.database.transactions.instance.PlayerTaxTransaction;
import org.leralix.tan.storage.database.transactions.instance.SalaryTransaction;
import org.leralix.tan.storage.database.transactions.instance.TerritoryTaxTransaction;
import org.leralix.tan.storage.database.transactions.instance.daily.DailyPlayerTaxTransaction;
import org.leralix.tan.storage.database.transactions.instance.daily.DailySalaryTransaction;
import org.leralix.tan.storage.database.transactions.instance.daily.DailyTerritoryTaxTransaction;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TerritoryTransactionHistory extends AbstractTransactionHistory {

    private final TerritoryData territoryData;

    public TerritoryTransactionHistory(
            Player player,
            TerritoryData territoryData,
            Consumer<Player> guiCallback
    ){
        super(player, guiCallback);
        this.territoryData = territoryData;
        open();
    }

    public TerritoryTransactionHistory(
            Player player,
            TerritoryData territoryData,
            TransactionType transactionType,
            Consumer<Player> guiCallback
    ){
        super(player, guiCallback, transactionType);
        this.territoryData = territoryData;
        open();
    }


    @Override
    protected EntityScope getScope() {
        return EntityScope.TERRITORY;
    }

    @Override
    protected String getID() {
        return territoryData.getID();
    }

    @Override
    protected List<AbstractTransaction> fetchTransactions() {
        List<AbstractTransaction> transactions = super.fetchTransactions();
        List<AbstractTransaction> result = new ArrayList<>();
        List<PlayerTaxTransaction> taxTransactions = new ArrayList<>();
        List<TerritoryTaxTransaction> territoryTaxTransactions = new ArrayList<>();
        List<SalaryTransaction> salaryTransactions = new ArrayList<>();

        for (AbstractTransaction transaction : transactions) {
            switch (transaction) {
                case PlayerTaxTransaction playerTaxTransaction -> taxTransactions.add(playerTaxTransaction);
                case TerritoryTaxTransaction territoryTaxTransaction -> territoryTaxTransactions.add(territoryTaxTransaction);
                case SalaryTransaction salaryTransaction -> salaryTransactions.add(salaryTransaction);
                default -> result.add(transaction);
            }
        }

        if (!taxTransactions.isEmpty()) {
            Map<LocalDate, List<PlayerTaxTransaction>> groupedTaxes = taxTransactions.stream()
                    .collect(Collectors.groupingBy(t ->
                            new Date(t.getDate()).toLocalDate()
                    ));

            for (List<PlayerTaxTransaction> dailyTaxes : groupedTaxes.values()) {
                result.add(new DailyPlayerTaxTransaction(dailyTaxes));
            }
        }

        if (!territoryTaxTransactions.isEmpty()) {
            Map<LocalDate, List<TerritoryTaxTransaction>> groupedTerritoryTaxes = territoryTaxTransactions.stream()
                    .collect(Collectors.groupingBy(t ->
                            new Date(t.getDate()).toLocalDate()
                    ));

            for (List<TerritoryTaxTransaction> dailyTaxes : groupedTerritoryTaxes.values()) {
                result.add(new DailyTerritoryTaxTransaction(dailyTaxes));
            }
        }

        if (!salaryTransactions.isEmpty()) {
            Map<LocalDate, List<SalaryTransaction>> groupedSalaries = salaryTransactions.stream()
                    .collect(Collectors.groupingBy(t ->
                            new Date(t.getDate()).toLocalDate()
                    ));

            for (List<SalaryTransaction> dailySalaries : groupedSalaries.values()) {
                result.add(new DailySalaryTransaction(dailySalaries));
            }
        }

        result.sort(Comparator.comparing(AbstractTransaction::getDate).reversed());
        return result;
    }
}
