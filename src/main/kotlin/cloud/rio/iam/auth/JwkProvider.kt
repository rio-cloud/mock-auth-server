package cloud.rio.iam.auth

import com.nimbusds.jose.jwk.JWK

interface JwkProvider {
    fun key(): JWK
}
