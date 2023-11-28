package net.chrisrichardson.liveprojects.servicetemplate.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(classes = {AccountServiceIntegrationTest.Config.class})
public class AccountServiceIntegrationTest {

    private final AccountService accountService;

    @Autowired
    public AccountServiceIntegrationTest(AccountService accountService) {
        this.accountService = accountService;
    }

    @Configuration
    @ComponentScan
    public static class Config {

    }

    @MockBean
    private AccountRepository accountRepository;

    @Test
    public void shouldConfigure() {

    }
}