package cloud.rio.iam.auth.internal

import cloud.rio.iam.auth.JwkProvider
import cloud.rio.iam.auth.WebKeysProvider
import com.nimbusds.jose.jwk.JWKSet


internal class JwksProvider(private val jwkProvider: JwkProvider) : WebKeysProvider {
    override fun jwks() = JWKSet(jwkProvider.key())
}
