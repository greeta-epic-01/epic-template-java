package net.chrisrichardson.liveprojects.servicetemplate.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Account {
    private long balance;
    private String owner;

    @Id
    @GeneratedValue
    private Long id;

    public Account() {
        this(0, "");
    }

    public Account(long balance, String owner) {
        this.balance = balance;
        this.owner = owner;
    }

    public long getBalance() {
        return balance;
    }

    public String getOwner() {
        return owner;
    }

    public Long getId() {
        return id;
    }

    public static AccountCommandResult createAccount(long balance, String owner) {
        if (balance <= 0) {
            return new AccountCommandResult.AmountNotGreaterThanZero(balance);
        } else {
            return new AccountCommandResult.AccountCreationSuccessful(new Account(balance, owner));
        }
    }

    public AccountCommandResult debit(long amount) {
        if (amount <= 0) {
            return new AccountCommandResult.AmountNotGreaterThanZero(amount);
        }

        if (amount > balance) {
            return new AccountCommandResult.BalanceExceeded(amount, balance);
        }

        balance -= amount;

        return AccountCommandResult.Success.INSTANCE;
    }

    public AccountCommandResult credit(long amount) {
        if (amount <= 0) {
            return new AccountCommandResult.AmountNotGreaterThanZero(amount);
        }

        balance += amount;

        return AccountCommandResult.Success.INSTANCE;
    }
}