package net.chrisrichardson.liveprojects.servicetemplate.componenttests

import io.restassured.RestAssured
import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.net.URI

@Testcontainers
class ServiceTemplateComponentTest {

    companion object {

        data class ContainerSpec(val hostPort : Int, val containerPort: Int) {
            fun hostUrl(path : String = "") = "http://localhost:${hostPort}${path}"
        }

        val serviceContainer = ContainerSpec(8080, 8080)
        val keycloakContainer = ContainerSpec(8091, 8091)
        val keycloakProxyContainer = ContainerSpec(8093, 80)


        @JvmStatic
        @Container
        val dockerComposeContainer = DockerComposeContainer<Nothing>(File("../docker-compose.yml")).apply {
            withExposedService("service-template_1", serviceContainer.containerPort, Wait.forHealthcheck())
            withExposedService("keycloak_1", keycloakContainer.containerPort, Wait.forHttp("/"))
            withExposedService("keycloak-test-proxy_1", keycloakProxyContainer.containerPort, Wait.forHttp("/"))
            withBuild(true)
        }
    }


    val realm = "service-template"
    val clientId = "service-template"

    fun fetchJwt(): String = given().proxy(URI(keycloakProxyContainer.hostUrl()))
            .urlEncodingEnabled(true)
            .param("client_id", clientId)
            .param("username", "foo")
            .param("password", "foopassword")
            .param("grant_type", "password")
            .header("Accept", ContentType.JSON.getAcceptHeader())
            .log().ifValidationFails()
            .post(keycloakContainer.hostUrl("/realms/$realm/protocol/openid-connect/token"))
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .assertThat()
            .body("access_token", not(nullValue()))
            .extract()
            .path("access_token")

    @Test
    fun shouldRetrieveAccounts() {
        given()
            .header("Authorization", "Bearer ${fetchJwt()}")
            .log().ifValidationFails()
            .get(serviceContainer.hostUrl("/accounts"))
            .then()
            .log().ifValidationFails()
            .statusCode(200)
    }

    @Test
    fun accountsRequireAuthentication() {
        get(serviceContainer.hostUrl("/accounts")).then().statusCode(401)
    }

    @Test
    fun shouldHavePrometheusMetrics() {
        get(serviceContainer.hostUrl("/actuator/prometheus")).then().statusCode(200)
    }


    @Test
    fun shouldHaveHealthCheckEndpoint() {
        get(serviceContainer.hostUrl("/actuator/health")).then().statusCode(200)
    }

    @Test
    fun theSwaggerUIShouldBeAccessible() {
        listOf("/swagger-ui/index.html")
                .forEach { get(serviceContainer.hostUrl(it)).then().statusCode(200) }

        get(serviceContainer.hostUrl("/v3/api-docs"))
          .then()
          .statusCode(200)
          .body("paths", notNullValue())
          .body("paths.size()", greaterThan(0))

    }


}
