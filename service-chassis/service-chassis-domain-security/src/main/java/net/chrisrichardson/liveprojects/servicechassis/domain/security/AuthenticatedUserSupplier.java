package net.chrisrichardson.liveprojects.servicechassis.domain.security;

import java.util.Set;
import java.util.function.Supplier;

public interface AuthenticatedUserSupplier extends Supplier<AuthenticatedUser> {

    class EMPTY_SUPPLIER implements AuthenticatedUserSupplier {
        @Override
        public AuthenticatedUser get() {
            return new AuthenticatedUser("nullId", Set.of());
        }
    }
}
