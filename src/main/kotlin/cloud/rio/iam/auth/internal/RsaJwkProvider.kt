package cloud.rio.iam.auth.internal

import cloud.rio.iam.auth.JwkProvider
import com.nimbusds.jose.Algorithm
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

internal class RsaJwkProvider : JwkProvider {

    private val rsaKey by lazy { createRsaKey() }

    override fun key(): RSAKey {
        return rsaKey
    }

    private fun createRsaKey(): RSAKey {
        // Generate the RSA key pair
        val gen = KeyPairGenerator.getInstance("RSA")
        gen.initialize(2048)
        val keyPair = gen.generateKeyPair()

        // Convert to JWK format
        return RSAKey.Builder(keyPair.public as RSAPublicKey)
            .privateKey(keyPair.private as RSAPrivateKey)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .algorithm(Algorithm("RS256"))
            .build()
    }
}
