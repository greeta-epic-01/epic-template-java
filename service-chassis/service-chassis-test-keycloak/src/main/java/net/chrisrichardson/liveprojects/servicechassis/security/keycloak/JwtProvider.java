package net.chrisrichardson.liveprojects.servicechassis.security.keycloak;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.CoreMatchers;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.not;

@Component
@ConditionalOnProperty("keycloak.auth-server-url")
public class JwtProvider {

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    private final URI httpProxy;

    private final Map<String, String> jwts = new HashMap<>();
    private final Map<String, String> ids = new HashMap<>();

    @Autowired
    public JwtProvider(
    @Value("${keycloak.auth-server-url}") String keycloakUrl,
    @Value("${keycloak.realm}") String realm,
    @Value("${keycloak.resource}") String clientId,
    URI httpProxy
    ) {
        this.keycloakUrl = keycloakUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.httpProxy = httpProxy;
    }

    public String getUserId(String userName) {
        return Objects.requireNonNull(ids.get(userName));
    }

    public String getJwt(String userName, String password, String role) {
        if (!jwts.containsKey(userName)) {
            ensureUserExists(userName, password, role);
            jwts.put(userName, fetchJwt(clientId, userName, password));
        }
        System.out.println("Issued jwt for " + userName + " = " + jwts.get(userName));
        return Objects.requireNonNull(jwts.get(userName));
    }

    private void ensureUserExists(String userName, String password, String role) {
        var keycloak = KeycloakBuilder.builder()
            .serverUrl(keycloakUrl)
            .realm("master")
            .grantType(OAuth2Constants.PASSWORD)
            .clientId("admin-cli")
            .username("admin")
            .password("admin")
            .build();

        var realmResource = keycloak.realm(realm);
        var usersResource = realmResource.users();

        var user = new UserRepresentation();
        user.setUsername(userName);
        user.setEnabled(true);

        var createUserResponse = usersResource.create(user);

        assertThat(createUserResponse.getStatus()).isIn(201, 409);

        var userId = Objects.requireNonNull(usersResource.search(userName).get(0).getId());

        var userResource = usersResource.get(userId);

        ids.put(userName, userId);

        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);

        userResource.resetPassword(cred);

        var rolesResource = realmResource.roles();

        var serviceTemplateRealmRole = Objects.requireNonNull(rolesResource.get(role)).toRepresentation();
        userResource.roles().realmLevel().add(Arrays.asList(serviceTemplateRealmRole));
    }

    private String fetchJwt(String clientId, String userName, String password) {
        return RestAssured.given()
            .proxy(httpProxy)
            .urlEncodingEnabled(true)
            .param("client_id", clientId)
            .param("username", userName)
            .param("password", password)
            .param("grant_type", "password")
            .header("Accept", ContentType.JSON.getAcceptHeader())
            .post(String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm))
            .then()
            .statusCode(200)
            .assertThat()
            .body("access_token", CoreMatchers.not(CoreMatchers.nullValue()))
            .extract()
            .path("access_token");
    }

    public String jwtForAuthorizedUser() {
        return getJwt(TestUserCredentials.userName, TestUserCredentials.password, "service-template-user");
    }

    public String jwtForOtherAuthorizedUser() {
        return getJwt(TestUserCredentials.otherAuthorizedUserName, TestUserCredentials.password, "service-template-user");
    }

    public String jwtForUserInSomeOtherRole() {
        return getJwt(TestUserCredentials.userNameOther, TestUserCredentials.passwordOther, "some-other-role");
    }
}

class TestUserCredentials {
    public static final String userName = "foo";
    public static final String password = "foopassword";
    public static final String otherAuthorizedUserName = "fooOtherAuthorized";

    public static final String userNameOther = "fooOther";
    public static final String passwordOther = "foopasswordOther";
}

class RestAssuredUtils {
    public static RequestSpecification givenJwt(String jwt) {
        return RestAssured.given().header("Authorization", "Bearer " + jwt);
    }
}