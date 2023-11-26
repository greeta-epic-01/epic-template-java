package net.chrisrichardson.liveprojects.servicechassis.domain.security

import java.util.function.Supplier

interface AuthenticatedUserSupplier : Supplier<AuthenticatedUser> {
    object EMPTY_SUPPLIER : AuthenticatedUserSupplier {
        override fun get() = AuthenticatedUser("nullId", emptySet())

    }
}

data class AuthenticatedUser(val id: String, val roles: Set<String>)

