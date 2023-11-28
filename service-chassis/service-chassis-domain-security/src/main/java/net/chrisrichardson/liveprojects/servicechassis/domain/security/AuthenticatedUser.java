package net.chrisrichardson.liveprojects.servicechassis.domain.security;

import java.util.Set;
import java.util.function.Supplier;

public class AuthenticatedUser {

    private final String id;
    private final Set<String> roles;

    public AuthenticatedUser(String id, Set<String> roles) {
        this.id = id;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
