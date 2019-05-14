package cloud.rio.iam.auth


private var mockAuthServer: MockAuthServer? = null

private fun mockAuthServerUrl(): String {
    return "http://localhost:${mockAuthServer?.port()}"
}

fun main() {
    mockAuthServer = MockAuthServer().apply { start() }

    System.setProperty(
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
        "${mockAuthServerUrl()}/.well-known/jwks.json"
    )
}