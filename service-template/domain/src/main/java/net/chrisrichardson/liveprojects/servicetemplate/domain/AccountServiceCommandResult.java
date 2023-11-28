package net.chrisrichardson.liveprojects.servicetemplate.domain;

public abstract class AccountServiceCommandResult {
    public static final class Success extends AccountServiceCommandResult {
        private final Account account;

        public Success(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }
    }

    public static final class AmountNotGreaterThanZero extends AccountServiceCommandResult {
        private final long amount;

        public AmountNotGreaterThanZero(long amount) {
            this.amount = amount;
        }

        public long getAmount() {
            return amount;
        }
    }

    public static final class BalanceExceeded extends AccountServiceCommandResult {
        private final long amount;
        private final long balance;

        public BalanceExceeded(long amount, long balance) {
            this.amount = amount;
            this.balance = balance;
        }

        public long getAmount() {
            return amount;
        }

        public long getBalance() {
            return balance;
        }
    }

    public static final class Unexpected extends AccountServiceCommandResult {
        private final AccountCommandResult outcome;

        public Unexpected(AccountCommandResult outcome) {
            this.outcome = outcome;
        }

        public AccountCommandResult getOutcome() {
            return outcome;
        }
    }

    public static final AccountServiceCommandResult AccountNotFound = new AccountServiceCommandResult() {
    };

    public static final AccountServiceCommandResult Unauthorized = new AccountServiceCommandResult() {
    };
}
