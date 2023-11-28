package net.chrisrichardson.liveprojects.servicetemplate.domain;

import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUser;
import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUserSupplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthenticatedUserSupplier authenticatedUserSupplier;

    @Mock
    private AccountServiceObserver accountServiceObserver;

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountServiceImpl(accountRepository, authenticatedUserSupplier, accountServiceObserver);
    }

    private final AuthenticatedUser authenticatedUser = new AuthenticatedUser("user-1010", Set.of());

    @Test
    void shouldCreate() {
        when(authenticatedUserSupplier.get()).thenReturn(authenticatedUser);

        ArgumentCaptor<Account> savedAccount = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.save(savedAccount.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        AccountServiceCommandResult outcome = accountService.createAccount(TestData.initialBalance);

        Assertions.assertThat(outcome).isEqualTo(new AccountServiceCommandResult.Success(savedAccount.getValue()));
    }
}