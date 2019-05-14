package cloud.rio.iam.auth

import com.nimbusds.jwt.JWTClaimsSet


interface JwtCreator {
    fun create(claimsSet: JWTClaimsSet): String
}
