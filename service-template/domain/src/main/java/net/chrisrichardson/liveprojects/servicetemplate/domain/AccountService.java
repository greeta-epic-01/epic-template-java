package net.chrisrichardson.liveprojects.servicetemplate.domain;

import java.util.List;

public interface AccountService {
    AccountServiceCommandResult createAccount(long initialBalance);
    AccountServiceCommandResult findAccount(long id);
    AccountServiceCommandResult debit(long id, long amount);
    AccountServiceCommandResult credit(long id, long amount);
    List<Account> findAllAccounts();
}
