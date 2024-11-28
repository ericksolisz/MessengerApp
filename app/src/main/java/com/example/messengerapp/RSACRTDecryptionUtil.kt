import java.math.BigInteger
import java.security.interfaces.RSAPrivateCrtKey

object RSADecryptCRTUtil {

    fun decryptMessageWithCRT(encryptedMessage: String, privateKey: RSAPrivateCrtKey): String {
        // Decodificar el mensaje cifrado
        val ciphertext = BigInteger(android.util.Base64.decode(encryptedMessage, android.util.Base64.DEFAULT))

        // Extraer parámetros del RSA-CRT
        val p = privateKey.primeP
        val q = privateKey.primeQ
        val dP = privateKey.primeExponentP
        val dQ = privateKey.primeExponentQ
        val qInv = privateKey.crtCoefficient

        // Realizar el descifrado usando CRT
        val m1 = ciphertext.modPow(dP, p)
        val m2 = ciphertext.modPow(dQ, q)
        val h = qInv.multiply(m1.subtract(m2)).mod(p)
        val plaintext = m2.add(h.multiply(q))

        // Convertir el resultado a cadena
        return String(plaintext.toByteArray())
    }
}
