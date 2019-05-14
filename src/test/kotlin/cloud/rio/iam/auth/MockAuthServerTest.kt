package cloud.rio.iam.auth

import assertk.assertThat
import assertk.assertions.*
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode


class MockAuthServerTest {

    private val client = OkHttpClient()

    @Test
    fun `start and stop runs server on a random port`() {
        // Given
        val uut = MockAuthServer()

        // When
        uut.start()
        val actual = uut.port()
        uut.close()

        // Then
        assertThat(actual).isNotZero()
    }

    @Test
    fun `auto closeable works`() {
        // Given
        val uut = MockAuthServer()

        // When
        uut.use {
            uut.start()
            assertThat(uut.port()).isNotZero()
        }

        // Then
        assertThat(uut.port()).isZero()
    }

    @Test
    fun `JWKS endpoint creates valid response`() {
        MockAuthServer().use {

            // Given
            it.start()

            val request = Request.Builder()
                .url("http://localhost:${it.port()}/.well-known/jwks.json")
                .build()

            // When
            client.newCall(request).execute().use { response ->
                val actualBody = response.body()?.string()

                // Then
                assertThat(response.isSuccessful).isTrue()
                assertThat(actualBody).isNotNull()

                val jwkSet = JWKSet.parse(actualBody)

                assertThat(jwkSet.keys).hasSize(1)
            }
        }

    }

    @Test
    fun `Token endpoint creates signed token including claims`() {
        MockAuthServer().use {

            // Given
            it.start()

            val requestBody = """
                { "sub": "some-subject", "scope": "openid acme" }
            """.trimIndent()

            val request = Request.Builder()
                .url("http://localhost:${it.port()}/token")
                .method("POST", RequestBody.create(MediaType.get("application/json"), requestBody))
                .build()

            // When
            client.newCall(request).execute().use { response ->
                val actualBody = response.body()?.string()

                // Then
                assertThat(response.isSuccessful).isTrue()
                assertThat(actualBody).isNotNull()

                val token = JWTParser.parse(actualBody) as SignedJWT
                assertThat(token).isNotNull()

                assertThat(token.jwtClaimsSet.claims["sub"]).isEqualTo("some-subject")
                assertThat(token.jwtClaimsSet.claims["scope"]).isEqualTo("openid acme")
                assertThat(token.state).isEqualTo(JWSObject.State.SIGNED)
            }
        }
    }

    @Test
    fun `OpenID configuration returns expected response`() {
        MockAuthServer().use {

            // Given
            it.start()

            val expectedBody = """
                {
                    "issuer": "https://localhost",
                    "authorization_endpoint": "https://localhost:${it.port()}/authorize",
                    "token_endpoint": "https://localhost:${it.port()}/token",
                    "response_types_supported": ["id_token token","id_token","token","code"],
                    "jwks_uri": "https://localhost:${it.port()}/.well-known/jwks.json",
                    "subject_types_supported": ["public"],
                    "id_token_signing_alg_values_supported": ["RS256"]
                }
            """.trimIndent()

            val request = Request.Builder()
                .url("http://localhost:${it.port()}/.well-known/openid-configuration")
                .build()

            // When
            client.newCall(request).execute().use { response ->
                val actualBody = response.body()?.string()

                // Then
                assertThat(response.isSuccessful).isTrue()
                assertThat(response.header("Content-Type")).isEqualTo("application/json")
                assertThat(actualBody).isNotNull()

                JSONAssert.assertEquals(expectedBody, actualBody!!, JSONCompareMode.LENIENT)
            }
        }

    }
}
