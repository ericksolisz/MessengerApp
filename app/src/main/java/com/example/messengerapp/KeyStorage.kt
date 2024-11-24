import android.content.Context

object KeyStorage {

    private const val PREFS_NAME = "key_storage_prefs"
    private const val PRIVATE_KEY = "privateKey"

    // Guardar clave privada
    fun savePrivateKey(context: Context, privateKey: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(PRIVATE_KEY, privateKey).apply()
    }

    // Recuperar clave privada
    fun getPrivateKey(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PRIVATE_KEY, null)
    }
}
