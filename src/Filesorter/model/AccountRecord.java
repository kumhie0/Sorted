package Filesorter.model;

public class AccountRecord {

    private final String account;
    private final String accountNumber;

    public AccountRecord(String account, String accountNumber) {
        this.account = account;
        this.accountNumber = accountNumber;
    }

    public String getAccount() {
        return account;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return account + "," + accountNumber;
    }
}