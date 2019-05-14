package cloud.rio.iam.auth

import com.nimbusds.jose.jwk.JWKSet


interface WebKeysProvider {
    fun jwks(): JWKSet
}
