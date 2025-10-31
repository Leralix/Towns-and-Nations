package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.TransactionType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PaymentTransaction extends AbstractTransaction {


    private String senderID;
    private String receiverID;
    private double amount;

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
        return TransactionType.PAYMENT;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.PLAYER_HEAD_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(senderID + " - " + receiverID, Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    @Override
    protected void fromResultSet(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.senderID = rs.getString("sender_id");
        this.receiverID = rs.getString("receiver_id");
        this.amount = rs.getDouble("amount");
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_payment (timestamp, sender_id, receiver_id, amount) VALUES (?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, senderID);
        ps.setString(3, receiverID);
        ps.setDouble(4, amount);
    }

    @Override
    public List<String> getConcerned() {
        List<String> res = new ArrayList<>();
        res.add(senderID);
        res.add(receiverID);
        return res;
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
