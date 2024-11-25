package com.example.messengerapp

import MessageStorage
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.messengerapp.ui.theme.MessengerAppTheme
import com.google.firebase.auth.FirebaseAuth

class LogIn : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var btnEraseD: Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        //habilitar solo para borrar mensajes locales
        //


        mAuth= FirebaseAuth.getInstance() //Intialice firebas Auth
        edtEmail= findViewById(R.id.edt_email)
        edtPassword= findViewById(R.id.edt_password)
        btnLogin= findViewById(R.id.btn_login)
        btnSignUp= findViewById(R.id.btn_sign_up)
        btnEraseD= findViewById(R.id.btn_erase)

        btnSignUp.setOnClickListener{
            val intent = Intent (this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{
            val email= edtEmail.text.toString()
            val password = edtPassword.text.toString()


            login(email, password);
        }

        btnEraseD.setOnClickListener{
            //borrar datos locales de conversacion
            MessageStorage.clearAllMessages(this)
            Log.d("boton borrar","Coversaciones locales borradas, borra datos de firebase para evitar errores")
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Recuperar clave privada del usuario actual
                    val uid = FirebaseAuth.getInstance().currentUser?.uid!!
                    val privateKeyString = KeyStorage.getPrivateKey(this, uid)

                    // Recuperar mensajes enviados del usuario
                    val sentMessages = MessageStorage.getMessages(this, uid)





                    if (privateKeyString == null) {
                        Toast.makeText(this, "Clave privada no encontrada para este usuario.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Continuar con la navegación a la pantalla principal
                        val intent = Intent(this@LogIn, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }

}


