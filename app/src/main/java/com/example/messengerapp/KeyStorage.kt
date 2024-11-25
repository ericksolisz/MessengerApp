import android.content.Context

object KeyStorage {

    private const val PREFS_NAME = "key_storage_prefs"

    // Guardar clave privada asociada a un UID
    fun savePrivateKey(context: Context, uid: String, privateKey: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(uid, privateKey).apply()
    }

    // Recuperar clave privada asociada a un UID
    fun getPrivateKey(context: Context, uid: String): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(uid, null)
    }
}
