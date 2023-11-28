package net.chrisrichardson.liveprojects.servicetemplate.domain;

public abstract class AccountCommandResult {
    public static final class AccountCreationSuccessful extends AccountCommandResult {
        private final Account account;

        public AccountCreationSuccessful(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }
    }

    public static final class Success extends AccountCommandResult {

        public static final Success INSTANCE = new Success();

        private Success() {

        }
    }

    public static final class AmountNotGreaterThanZero extends AccountCommandResult {
        private final long amount;

        public AmountNotGreaterThanZero(long amount) {
            this.amount = amount;
        }

        public long getAmount() {
            return amount;
        }
    }

    public static final class BalanceExceeded extends AccountCommandResult {
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

    public static final class Unauthorized extends AccountCommandResult {
    }
}
