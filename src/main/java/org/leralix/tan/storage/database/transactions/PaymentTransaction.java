package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.time.LocalDateTime;

public class PaymentTransaction extends AbstractTransaction {


    private final String senderID;
    private final String receiverID;
    private final double amount;

    public PaymentTransaction(String senderID, String receiverID, double amount){
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
    }

    public PaymentTransaction(LocalDateTime localDate, String senderID, String receiverID, double amount){
        super(localDate);
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
    }

    public TransactionType getType(){
        return TransactionType.DONATION;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.PLAYER_HEAD_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(senderID + " - " + receiverID, Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    public String getSenderID() {
        return senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public double getAmount() {
        return amount;
    }
}
