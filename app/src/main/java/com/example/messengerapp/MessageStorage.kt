import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MessageStorage {

    private const val PREFS_NAME = "message_storage_prefs"

    // Guardar mensajes enviados asociados a un UID
    fun saveMessages(context: Context, uid: String, messages: List<String>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonMessages = Gson().toJson(messages) // Convertir lista de mensajes a JSON
        sharedPreferences.edit().putString(uid, jsonMessages).apply()
    }

    // Recuperar mensajes enviados asociados a un UID
    fun getMessages(context: Context, uid: String?): List<String> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonMessages = sharedPreferences.getString(uid, null)
        return if (jsonMessages != null) {
            val type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(jsonMessages, type) // Convertir JSON de vuelta a lista
        } else {
            emptyList() // Retornar lista vacía si no hay mensajes guardados
        }
    }
}
