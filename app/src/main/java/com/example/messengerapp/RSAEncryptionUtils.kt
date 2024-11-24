import java.security.Key
import javax.crypto.Cipher
import android.util.Base64

object RSAEncryptionUtil {

    // Método para cifrar un mensaje con la clave pública
    fun encryptMessage(message: String, publicKey: Key): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray())
        // Usa Base64 de android.util para codificar
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // Método para descifrar un mensaje con la clave privada
    fun decryptMessage(encryptedMessage: String, privateKey: Key): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        // Decodifica con Base64 de android.util
        val decodedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }
}