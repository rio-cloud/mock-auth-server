package cloud.rio.iam.auth

import cloud.rio.iam.auth.internal.JwksProvider
import cloud.rio.iam.auth.internal.RsaJwkProvider
import cloud.rio.iam.auth.internal.RsaSignedJwtCreator
import com.nimbusds.jwt.JWTClaimsSet
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.time.LocalDateTime
import java.time.ZoneOffset


typealias ClaimsMessage = Map<String, Any?>

class MockAuthServer(
    private val hostName: String = "localhost",
    private val schema: String = "http",
    private val keyProvider: JwkProvider = RsaJwkProvider(),
    private val jwksProvider: WebKeysProvider = JwksProvider(keyProvider),
    private val tokenCreator: JwtCreator = RsaSignedJwtCreator(keyProvider)
) : AutoCloseable {

    private val claimsBodyLens = Body.auto<ClaimsMessage>().toLens()

    private val routes = routes(
        JWKS_ENDPOINT bind Method.GET to { handleJWKS() },
        OPENID_CONFIGURATION_ENDPOINT bind Method.GET to { handleOpenIDConfiguration() },
        TOKEN_ENDPOINT bind Method.POST to { req: Request -> handleToken(req) }
    )

    private var server: Http4kServer? = null

    fun start(port: Int = 0) {
        server?.stop()
        server = routes.asServer(Jetty(port)).apply { start() }
    }

    override fun close() {
        return server?.stop().let { server = null }
    }

    fun port(): Int {
        return server?.port() ?: 0
    }

    private fun handleJWKS(): Response {
        return Response(OK).body(jwksProvider.jwks().toJSONObject().toJSONString())
    }

    private fun handleOpenIDConfiguration(): Response {
        val port = when (port()) {
            0, 443, 80 -> ""
            else -> ":${port()}"
        }

        return Response(OK).header("Content-Type", "application/json").body(
            """
            {
                "issuer": "$schema://$hostName$port",
                "authorization_endpoint": "$schema://$hostName$port/authorize",
                "token_endpoint": "$schema://$hostName$port$TOKEN_ENDPOINT",
                "response_types_supported": ["id_token token","id_token","token","code"],
                "jwks_uri": "$schema://$hostName$port$JWKS_ENDPOINT",
                "subject_types_supported": ["public"],
                "id_token_signing_alg_values_supported": ["RS256"]
            }
        """.trimIndent()
        )
    }

    private fun handleToken(req: Request): Response {
        return Response(OK).body(tokenCreator.create(claimsBodyLens(req).toJWTClaimSet()))
    }

    companion object {
        private const val JWKS_ENDPOINT = "/.well-known/jwks.json"
        private const val OPENID_CONFIGURATION_ENDPOINT = "/.well-known/openid-configuration"
        private const val TOKEN_ENDPOINT = "/token"
    }

}

data class ExampleClaims(
    val sub: String = "example",
    val scope: String = "openid email profile",
    val iss: String = "example.com",
    val iat: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val exp: Long = LocalDateTime.ofEpochSecond(iat, 0, ZoneOffset.UTC)
        .plusSeconds(60).toEpochSecond(ZoneOffset.UTC),
    val account: String = "acme",
    val tenant: String = "prod",
    val azp: String = "client-id"
)

private fun ClaimsMessage.toJWTClaimSet(): JWTClaimsSet {
    return JWTClaimsSet.Builder().apply {
        this@toJWTClaimSet.forEach { (key, value) -> claim(key, value) }
    }.build()
}
