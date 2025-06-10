package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;

public class TransactionDAO {
    private static final String FILE_PATH = "transactions.dat";

    public static void saveTransactions(List<Transaction> transactions) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(transactions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> loadTransactions() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Transaction>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void addTransaction(Transaction transaction) {
        List<Transaction> transactions = loadTransactions();
        transactions.add(transaction);
        saveTransactions(transactions);
    }

    public static List<Transaction> getTransactionsByUser(String userId) {
        List<Transaction> userTransactions = new ArrayList<>();
        List<Transaction> allTransactions = loadTransactions();
        
        for (Transaction t : allTransactions) {
            if (t.getSenderId().equalsIgnoreCase(userId) || t.getReceiverId().equalsIgnoreCase(userId)) {
                userTransactions.add(t);
            }
        }
        return userTransactions;
    }
}   