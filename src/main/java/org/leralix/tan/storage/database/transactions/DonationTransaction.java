package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.time.LocalDateTime;

public class DonationTransaction extends AbstractTransaction {


    private final String playerID;
    private final String territoryID;
    private final double amount;

    public DonationTransaction(TerritoryData territoryData, Player player, double amount){
        this.playerID = territoryData.getID();
        this.territoryID = player.getUniqueId().toString();
        this.amount = amount;
    }

    public DonationTransaction(LocalDateTime localDate, String playerID, String territoryID, double amount){
        super(localDate);
        this.playerID = playerID;
        this.territoryID = territoryID;
        this.amount = amount;
    }

    public TransactionType getType(){
        return TransactionType.DONATION;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.BUDGET_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(playerID + " - " + territoryID, Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTerritoryID() {
        return territoryID;
    }

    public double getAmount() {
        return amount;
    }
}
