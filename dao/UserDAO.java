package dao;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import model.User;

public class UserDAO {
    private static final String FILE_PATH = "users.dat";

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users to " + FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                return (List<User>) obj;
            } else {
                System.err.println("Data in " + FILE_PATH + " is not a List of Users. Returning empty list.");
                return new ArrayList<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users from " + FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static User getUserByUsername(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public static boolean addUser(User user) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                return false;
            }
            if (u.getPhone().equals(user.getPhone())) {
                return false;
            }
        }
        users.add(user);
        saveUsers(users);
        return true;
    }

    public static boolean updateUser(User updatedUser) {
        List<User> users = loadUsers();
        boolean found = false;
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equalsIgnoreCase(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                found = true;
                break;
            }
        }
        
        if (found) {
            saveUsers(users);
            return true;
        }
        return false;
    }

    public static boolean updateUserPassword(String username, String newPassword) {
        List<User> users = loadUsers();
        boolean found = false;
        
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                user.setPassword(newPassword);
                found = true;
                break;
            }
        }
        
        if (found) {
            saveUsers(users);
            return true;
        }
        return false;
    }

    public static boolean deleteUser(String username) {
        List<User> users = loadUsers();
        boolean removed = false;
        
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equalsIgnoreCase(username)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (removed) {
            saveUsers(users);
            return true;
        }
        return false;
    }
}