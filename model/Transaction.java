package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    private String transactionId;
    private String senderId;
    private String receiverId;
    private double amount;
    private String timestamp;
    private String type;

    public Transaction(String senderId, String receiverId, double amount, String type) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.type = type;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.transactionId = "TXN" + System.currentTimeMillis();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public double getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: Rs.%.2f from %s to %s", timestamp, type, amount, senderId, receiverId);
    }
}