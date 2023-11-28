package net.chrisrichardson.liveprojects.servicechassis.security;

import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUser;
import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUserSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final String serviceRole;

    public SecurityConfig(@Value("${service.template.role}") String serviceRole) {
        this.serviceRole = serviceRole;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, List<String>> realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
            List<String> roles = realmAccess != null ? realmAccess.get("roles") : List.of();
            return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
        });
        return jwtConverter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/swagger**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/**").hasRole(serviceRole)
                .and()
                .oauth2ResourceServer().jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()));
    }

    @Bean
    public AuthenticatedUserSupplier authenticatedUserSupplier() {
        return new DefaultAuthenticatedUserSupplier();
    }
}

class DefaultAuthenticatedUserSupplier implements AuthenticatedUserSupplier {

    @Override
    public AuthenticatedUser get() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return new AuthenticatedUser(authentication.getName(),
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
    }
}