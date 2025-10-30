package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.time.LocalDateTime;

public class UpgradeTransaction extends AbstractTransaction {


    private final String territoryID;
    private final String upgradeID;
    private final int newLevel;
    private final double amount;

    public UpgradeTransaction(String territoryID, String upgradeID, int newLevel, double amount){
        this.territoryID = territoryID;
        this.upgradeID = upgradeID;
        this.newLevel = newLevel;
        this.amount = amount;
    }

    public UpgradeTransaction(LocalDateTime localDate, String territoryID, String upgradeID, int newLevel, double amount){
        super(localDate);
        this.territoryID = territoryID;
        this.upgradeID = upgradeID;
        this.newLevel = newLevel;
        this.amount = amount;
    }

    public TransactionType getType(){
        return TransactionType.DONATION;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.PLAYER_HEAD_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(territoryID + " - " + upgradeID + " - " + newLevel , Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    public String getTerritoryID() {
        return territoryID;
    }

    public String getUpgradeID() {
        return upgradeID;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public double getAmount() {
        return amount;
    }
}
