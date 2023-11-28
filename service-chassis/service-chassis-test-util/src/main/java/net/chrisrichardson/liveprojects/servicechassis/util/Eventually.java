package net.chrisrichardson.liveprojects.servicechassis.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Eventually {

    public static <T> T withConfiguration(int iterations, long sleepInMillis, Supplier<T> function) {
        return eventuallyInternal(iterations, sleepInMillis, function);
    }

    public static <T> T eventually(Supplier<T> function) {
        return withConfiguration(15, 1000, function);
    }

    private static <T> T eventuallyInternal(int iterations, long sleepInMillis, Supplier<T> function) {
        Throwable lastException = null;
        for (int n = 1; n <= iterations; n++) {
            try {
                return function.get();
            } catch (Exception | AssertionError e) {
                lastException = e;
            }
            if (n != iterations) {
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepInMillis);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RuntimeException(String.format("Eventually timed out after %s iterations, sleeping for %s ms", iterations, sleepInMillis), lastException);
    }
}