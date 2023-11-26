package net.chrisrichardson.liveprojects.servicechassis.security

import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUser
import net.chrisrichardson.liveprojects.servicechassis.domain.security.AuthenticatedUserSupplier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter


@Configuration
@EnableWebSecurity
class SecurityConfig(@Value("\${service.template.role}") val serviceRole: String) : WebSecurityConfigurerAdapter() {

    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val jwtConverter = JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter { jwt ->
            val realmAccess = jwt.claims["realm_access"] as Map<String, List<String>>?
            val roles = realmAccess?.get("roles")
            (roles ?: listOf()).map { SimpleGrantedAuthority("ROLE_$it") }
        }
        return jwtConverter
    }

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/swagger**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .antMatchers("/**")
             .hasRole(serviceRole)
            .and()
             .oauth2ResourceServer().jwt { jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()) }
    }

    @Bean
    fun authenticatedUserSupplier() : AuthenticatedUserSupplier = DefaultAuthenticatedUserSupplier()

}

class DefaultAuthenticatedUserSupplier : AuthenticatedUserSupplier {

    override fun get(): AuthenticatedUser {
        val authentication = SecurityContextHolder.getContext().authentication
        return AuthenticatedUser(authentication.name, authentication.authorities.map(GrantedAuthority::getAuthority).toSet())
    }
}
