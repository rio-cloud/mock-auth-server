package cloud.rio.iam.auth.internal

import cloud.rio.iam.auth.JwkProvider
import cloud.rio.iam.auth.JwtCreator
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet

internal class RsaSignedJwtCreator(private val jwkProvider: JwkProvider) : JwtCreator {

    override fun create(claimsSet: JWTClaimsSet): String {
        val claims = claimsSet.toJSONObject()
        val key = jwkProvider.key()

        val jwsObject = JWSObject(
            JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(key.keyID)
                .type(JOSEObjectType.JWT)
                .build(),
            Payload(claims)
        )

        if (key is RSAKey) {
            jwsObject.sign(RSASSASigner(key))
        } else {
            throw RuntimeException("Invalid RSAKey provided")
        }

        return jwsObject.serialize()
    }

}
