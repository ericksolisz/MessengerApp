import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import android.util.Base64

object RSAUtils {

    lateinit var publicKey: PublicKey
    lateinit var privateKey: PrivateKey

    // Generar un par de claves RSA (pública y privada)
    fun generateKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair: KeyPair = keyPairGenerator.generateKeyPair()
        publicKey = keyPair.public
        privateKey = keyPair.private
    }

    // Obtener la clave pública como String codificado en Base64
    fun getPublicKeyAsString(): String {
        return Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
    }

    // Obtener la clave privada como String codificado en Base64
    fun getPrivateKeyAsString(): String {
        return Base64.encodeToString(privateKey.encoded, Base64.DEFAULT)
    }

    // Reconstruir la clave pública desde un String codificado en Base64
    fun getPublicKeyFromString(key: String): PublicKey {
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val keySpec = java.security.spec.X509EncodedKeySpec(keyBytes)
        return java.security.KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }

    // Reconstruir la clave privada desde un String codificado en Base64
    fun getPrivateKeyFromString(key: String): PrivateKey {
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val keySpec = java.security.spec.PKCS8EncodedKeySpec(keyBytes)
        return java.security.KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }
}