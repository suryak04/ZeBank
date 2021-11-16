package com.example.zebank.service;

import com.example.zebank.entity.AccountDetails;
import com.example.zebank.exception.AccountNotFoundException;

import javax.swing.text.html.parser.Entity;
import java.sql.*;
import java.util.Scanner;

public class AccountService {

    public static final String URLTOCONNECT = "jdbc:mysql://localhost:3306/test";

    public static final String USERNAME = "root";

    public static final String USERPASSWORD = "root";

    public static final Scanner sc = new Scanner(System.in);

    String qry;

    Connection dbCon;

    Statement theStatement;

    PreparedStatement thePreparedStatement;

    ResultSet theResultSet;

    public AccountService() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            dbCon = DriverManager.getConnection(URLTOCONNECT, USERNAME, USERPASSWORD);

            theStatement = dbCon.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        AccountService accountService = new AccountService();

        System.out.println("Choose Option");
        System.out.println(" 1. Create Account \n 2. Balance \n 3. Deposit Amount \n 4. Withdraw Amount " +
                "\n 5. Fund Transfer \n 6. Transactions");
        int option = Integer.parseInt(sc.nextLine());
        if (option == 1){
            System.out.println("Enter Account Id");
            String accountId = sc.nextLine();
            System.out.println("Enter User Name");
            String userName = sc.nextLine();
            System.out.println("Enter the opening balance to deposit");
            Double amount = Double.parseDouble(sc.nextLine());

            AccountDetails accountDetails = new AccountDetails(accountId, userName, amount);

            if (accountService.createAccount(accountDetails)){
                System.out.println("account added sucessfully");
            }
            else {
                System.out.println("unable to add user");
            }
        }
        else if (option == 2){
            System.out.println("Enter Account Id");
            String accountId = sc.nextLine();
            System.out.println("Total balance");
            System.out.println(accountService.getBalance(accountId));
        }
        else if (option == 3){
            System.out.println("Enter Account Id");
            String accountId = sc.nextLine();
            System.out.println("Enter deposit amount");
            Double amount = Double.parseDouble(sc.nextLine());

            if (accountService.deposit(accountId, amount)){
                Double debit = 0.0;
                Double credit = amount;
                accountService.addTransaction(accountId, debit, credit);
                System.out.println("Amount deposited sucessfully");
                System.out.println("Total Balance = "+accountService.getBalance(accountId));
            }
            else{
                System.out.println("Amount not deposited");
            }
        }
        else if (option == 4){
            System.out.println("Enter Account Id");
            String accountId = sc.nextLine();
            System.out.println("Enter Withdraw amount");
            Double amount = Double.parseDouble(sc.nextLine());

            if (accountService.withdraw(accountId, amount)){
                Double debit = amount;
                Double credit = 0.0;
                accountService.addTransaction(accountId, debit, credit);
                System.out.println("Amount withdrawl successful");
            }else {
                System.out.println("Amount withdrawl failed");
            }
        }
        else if (option == 5){
            System.out.println("Enter From Account Id");
            String fromAccountId = sc.nextLine();
            System.out.println("Enter To Account Id");
            String toAccountId = sc.nextLine();
            System.out.println("Enter Transfer Amount");
            Double transferAmount = Double.parseDouble(sc.nextLine());

            if (accountService.fundTransfer(fromAccountId, toAccountId, transferAmount)){
                accountService.addTransaction(fromAccountId, transferAmount, 0.0);
                accountService.addTransaction(toAccountId, 0.0, transferAmount);
                System.out.println("Fund Transfer successful");
            }else {
                System.out.println("Fund Transfer Failed");
            }
        }
        else if (option == 6){
            System.out.println("Enter Account Id");
            String accountId = sc.nextLine();
            accountService.transactions(accountId);
        }

    }

    public boolean createAccount(AccountDetails accountDetails){

        if(accountDetails.getAccountId().length() != 12){
            System.out.println("Length of Account id should be = 12");
            return false;
        }

        qry = "insert into zebank values(?, ?, ?)";

        try {
            thePreparedStatement = dbCon.prepareStatement(qry);

            thePreparedStatement.setString(1,accountDetails.getAccountId());
            thePreparedStatement.setString(2,accountDetails.getUserName());
            thePreparedStatement.setDouble(3,accountDetails.getBalance());

            if(thePreparedStatement.executeUpdate() > 0){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Unable to create account "+ e.getMessage());
            return false;
        }

        return true;
    }

    public Double getBalance(String accountId) throws Exception{
        Double balance = -1.0;
        qry = "select balance from zebank where account_id = ?";
        try {
            thePreparedStatement = dbCon.prepareStatement(qry);
            thePreparedStatement.setString(1,accountId);
            theResultSet = thePreparedStatement.executeQuery();
            while (theResultSet.next()){
                balance = theResultSet.getDouble(1);
            }

            if (balance < 0.0){
                throw new AccountNotFoundException();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return balance;
    }

    public boolean deposit(String accountId, Double depositAmount) throws Exception{

        if(depositAmount <= 0){
            System.out.println("Amount shoulde be > 0");
            return false;
        }

        Double actualBalance = getBalance(accountId);

        qry = "update zebank set balance = ? where account_id = ?";

        try {
            thePreparedStatement = dbCon.prepareStatement(qry);
            thePreparedStatement.setDouble(1,actualBalance+depositAmount);
            thePreparedStatement.setString(2,accountId);

            thePreparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("unable to deposit amount" + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean withdraw(String accountId, Double withdrawAmount) throws Exception{

        if(withdrawAmount <= 0){
            System.out.println("Withdraw amount shoulde be > 0");
            return false;
        }

        Double actualBalance = getBalance(accountId);

        if(actualBalance < withdrawAmount){
            System.out.println("Insufficent Balance");
            return false;
        }

        qry = "update zebank set balance = ? where account_id = ?";
        try {
            thePreparedStatement = dbCon.prepareStatement(qry);
            thePreparedStatement.setDouble(1,actualBalance-withdrawAmount);
            thePreparedStatement.setString(2,accountId);

            if(thePreparedStatement.executeUpdate() > 0){
                System.out.println("Remaining amount = "+(actualBalance-withdrawAmount));
            }
        } catch (SQLException e) {
            System.out.println("Unable to withdraw "+e.getMessage());
            return false;
        }

        return true;
    }

    public boolean fundTransfer(String fromAccountId, String toAccountId, Double transferAmount)
            throws Exception{

        if(withdraw(fromAccountId, transferAmount) && deposit(toAccountId, transferAmount)){
            return true;
        }

        return false;
    }

    public void transactions(String accountId){
        System.out.println("These are transactions");
        qry = "select account_id, debit, credit, balance from zebank_trans where account_id = ?";

        try {
            thePreparedStatement = dbCon.prepareStatement(qry);
            thePreparedStatement.setString(1,accountId);
            theResultSet = thePreparedStatement.executeQuery();
            System.out.println("Account id \t Debit \t Credit");
            while (theResultSet.next()){
                System.out.println(theResultSet.getString(1)+"\t"+
                        theResultSet.getDouble(2)+"\t"+
                        theResultSet.getDouble(3));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void addTransaction(String accountId, Double debit, Double credit) throws Exception{

        qry = "insert into zebank_trans(account_id, debit, credit) values(?, ?, ?)";
        Double balance = getBalance(accountId);
        try {
            thePreparedStatement = dbCon.prepareStatement(qry);
            thePreparedStatement.setString(1,accountId);
            thePreparedStatement.setDouble(2,debit);
            thePreparedStatement.setDouble(3,credit);
//            thePreparedStatement.setDouble(4,balance);

            thePreparedStatement.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("unable to add transaction" + e.getMessage());
        }


    }

}
