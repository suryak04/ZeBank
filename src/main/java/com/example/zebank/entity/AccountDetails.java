package com.example.zebank.entity;

public class AccountDetails {
    private String accountId;
    private String userName;
    private Double balance;

    public AccountDetails(String accountId, String userName, Double balance) {
        this.accountId = accountId;
        this.userName = userName;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return  "accountId='" + accountId + '\'' +
                ", userName='" + userName + '\'' +
                ", balance=" + balance;
    }
}
