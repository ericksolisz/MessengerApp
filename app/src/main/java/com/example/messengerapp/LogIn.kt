package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
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

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()


        mAuth= FirebaseAuth.getInstance() //Intialice firebas Auth
        edtEmail= findViewById(R.id.edt_email)
        edtPassword= findViewById(R.id.edt_password)
        btnLogin= findViewById(R.id.btn_login)
        btnSignUp= findViewById(R.id.btn_sign_up)

        btnSignUp.setOnClickListener{
            val intent = Intent (this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{
            val email= edtEmail.text.toString()
            val password = edtPassword.text.toString()


            login(email, password);
        }
    }

    private fun login(email: String, password: String){
        ///logic for login (firebase doc)
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this@LogIn, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LogIn, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }

    }
}


