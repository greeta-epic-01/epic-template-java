package net.chrisrichardson.liveprojects.servicetemplate.domain;

import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUserSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuthenticatedUserSupplier authenticatedUserSupplier;
    private final AccountServiceObserver accountServiceObserver;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AuthenticatedUserSupplier authenticatedUserSupplier,
                              AccountServiceObserver accountServiceObserver) {
        this.accountRepository = accountRepository;
        this.authenticatedUserSupplier = authenticatedUserSupplier;
        this.accountServiceObserver = accountServiceObserver;
    }

    @Override
    public AccountServiceCommandResult createAccount(long initialBalance) {
        AccountCommandResult outcome = Account.createAccount(initialBalance, currentUserId());
        if (outcome instanceof AccountCommandResult.AccountCreationSuccessful) {
            accountServiceObserver.noteAccountCreated();
            return new AccountServiceCommandResult.Success(accountRepository.save(((AccountCommandResult.AccountCreationSuccessful) outcome).getAccount()));
        } else if (outcome instanceof AccountCommandResult.AmountNotGreaterThanZero) {
            return new AccountServiceCommandResult.AmountNotGreaterThanZero(initialBalance);
        } else {
            return new AccountServiceCommandResult.Unexpected(outcome);
        }
    }

    private String currentUserId() {
        return authenticatedUserSupplier.get().getId();
    }

    @Override
    public AccountServiceCommandResult findAccount(long id) {
        Optional<AccountServiceCommandResult> accountOptional = withAuthorizedAccess(id, account -> new AccountServiceCommandResult.Success(account));
        return accountOptional.map(accountServiceCommandResult -> accountServiceCommandResult)
                .orElseGet(() -> AccountServiceCommandResult.AccountNotFound);
    }

    @Override
    public AccountServiceCommandResult debit(long id, long amount) {
        Optional<AccountServiceCommandResult> result = withAuthorizedAccess(id, account -> {
            AccountCommandResult outcome = account.debit(amount);
            if (outcome instanceof AccountCommandResult.Success) {
                accountServiceObserver.noteSuccessfulDebit();
                return new AccountServiceCommandResult.Success(account);
            } else if (outcome instanceof AccountCommandResult.AmountNotGreaterThanZero) {
                accountServiceObserver.noteFailedDebit();
                return new AccountServiceCommandResult.AmountNotGreaterThanZero(amount);
            } else if (outcome instanceof AccountCommandResult.BalanceExceeded) {
                accountServiceObserver.noteFailedDebit();
                return new AccountServiceCommandResult.BalanceExceeded(amount, ((AccountCommandResult.BalanceExceeded) outcome).getBalance());
            } else if (outcome instanceof AccountCommandResult.Unauthorized) {
                accountServiceObserver.noteFailedDebit();
                return AccountServiceCommandResult.Unauthorized;
            } else {
                return new AccountServiceCommandResult.Unexpected(outcome);
            }
        });
        return result.orElseGet(() -> {
            accountServiceObserver.noteFailedDebit();
            return AccountServiceCommandResult.AccountNotFound;
        });
    }

    @Override
    public AccountServiceCommandResult credit(long id, long amount) {
        Optional<AccountServiceCommandResult> result = withAuthorizedAccess(id, account -> {
            AccountCommandResult outcome = account.credit(amount);
            if (outcome instanceof AccountCommandResult.Success) {
                accountServiceObserver.noteSuccessfulCredit();
                return new AccountServiceCommandResult.Success(account);
            } else if (outcome instanceof AccountCommandResult.AmountNotGreaterThanZero) {
                accountServiceObserver.noteFailedCredit();
                return new AccountServiceCommandResult.AmountNotGreaterThanZero(amount);
            } else if (outcome instanceof AccountCommandResult.Unauthorized) {
                accountServiceObserver.noteFailedDebit();
                return AccountServiceCommandResult.Unauthorized;
            } else {
                return new AccountServiceCommandResult.Unexpected(outcome);
            }
        });
        return result.orElseGet(() -> {
            accountServiceObserver.noteUnauthorizedAccountAccess();
            return AccountServiceCommandResult.AccountNotFound;
        });
    }

    @Override
    public List<Account> findAllAccounts() {
        List<Account> result = accountRepository.findByOwner(currentUserId());
        return result;
    }

    private Optional<AccountServiceCommandResult> withAuthorizedAccess(long id, java.util.function.Function<Account, AccountServiceCommandResult> function) {
        return accountRepository.findById(id).map(account -> {
            if (!account.getOwner().equals(currentUserId())) {
                accountServiceObserver.noteUnauthorizedAccountAccess();
                return AccountServiceCommandResult.Unauthorized;
            } else {
                return function.apply(account);
            }
        });
    }
}

interface AccountServiceObserver {
    void noteAccountCreated();
    void noteSuccessfulDebit();
    void noteFailedDebit();
    void noteFailedCredit();
    void noteSuccessfulCredit();
    void noteUnauthorizedAccountAccess();
}

