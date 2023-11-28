package net.chrisrichardson.liveprojects.servicetemplate.domain;

public class TestData {

    public static final long accountId = 99L;
    public static final long initialBalance = 101L;
    public static final long debitAmount = 11L;
    public static final long creditAmount = 5L;
    public static final long balanceAfterDebit = initialBalance - debitAmount;
    public static final long balanceAfterCredit = balanceAfterDebit + creditAmount;

    private TestData() {
        // Private constructor to prevent instantiation
    }
}
