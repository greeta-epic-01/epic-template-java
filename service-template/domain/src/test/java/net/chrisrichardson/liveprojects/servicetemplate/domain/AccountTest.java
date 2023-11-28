package net.chrisrichardson.liveprojects.servicetemplate.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void shouldDebitAndCredit() {
        Account account = new Account(TestData.initialBalance, "owner");
        AccountCommandResult result = account.debit(TestData.debitAmount);

        assertThat(result).isEqualTo(AccountCommandResult.Success.INSTANCE);
        assertThat(account.getBalance()).isEqualTo(TestData.balanceAfterDebit);

        AccountCommandResult creditResult = account.credit(TestData.creditAmount);
        assertThat(creditResult).isEqualTo(AccountCommandResult.Success.INSTANCE);
        assertThat(account.getBalance()).isEqualTo(TestData.balanceAfterCredit);
    }

    @Test
    void shouldDebitCurrentBalance() {
        Account account = new Account(TestData.initialBalance, "owner");
        AccountCommandResult result = account.debit(TestData.initialBalance);
        assertThat(result).isEqualTo(AccountCommandResult.Success.INSTANCE);
        assertThat(account.getBalance()).isEqualTo(0);
    }

    @Test
    void shouldDebitCurrentBalanceMinus1() {
        Account account = new Account(TestData.initialBalance, "owner");
        AccountCommandResult result = account.debit(TestData.initialBalance - 1);
        assertThat(result).isEqualTo(AccountCommandResult.Success.INSTANCE);
        assertThat(account.getBalance()).isEqualTo(1);
    }

    @Test
    void shouldDebitCurrentBalancePlusShouldFail() {
        Account account = new Account(TestData.initialBalance, "owner");
        AccountCommandResult result = account.debit(TestData.initialBalance + 1);
        assertThat(result).isEqualTo(new AccountCommandResult.BalanceExceeded(TestData.initialBalance + 1, TestData.initialBalance));
        assertThat(account.getBalance()).isEqualTo(TestData.initialBalance);
    }

    @Test
    void debitZeroShouldFail() {
        Account account = new Account(TestData.initialBalance, "owner");
        long amount = 0L;
        AccountCommandResult result = account.debit(amount);
        assertThat(result).isEqualTo(new AccountCommandResult.AmountNotGreaterThanZero(amount));
        assertThat(account.getBalance()).isEqualTo(TestData.initialBalance);
    }

    @Test
    void creditZeroShouldFail() {
        Account account = new Account(TestData.initialBalance, "owner");
        long amount = 0L;
        AccountCommandResult result = account.credit(amount);
        assertThat(result).isEqualTo(new AccountCommandResult.AmountNotGreaterThanZero(amount));
        assertThat(account.getBalance()).isEqualTo(TestData.initialBalance);
    }
}