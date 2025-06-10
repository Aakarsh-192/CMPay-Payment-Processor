package model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String phone;
    private double balance;

    public User(String username, String password, String phone, double balance) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", balance=" + balance +
                '}';
    }

    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}