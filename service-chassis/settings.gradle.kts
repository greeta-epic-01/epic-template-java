rootProject.name = "service-chassis"

include("service-chassis-dependencies-bom")

include("service-chassis-util")

include("service-chassis-test-keycloak")
include("service-chassis-test-util")
include("service-chassis-test-containers")

include("service-chassis-domain-security")

include("service-chassis-persistence")
include("service-chassis-web")
include("service-chassis-web-swagger")

include("service-chassis-web-security")
include("service-chassis-health-check")
include("service-chassis-metrics")
include("service-chassis-distributed-tracing")

include("service-chassis-bom")

include("service-chassis-plugins")
