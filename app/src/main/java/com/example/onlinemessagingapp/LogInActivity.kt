package com.example.onlinemessagingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LogInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        auth = Firebase.auth

        var logIn_button : Button = findViewById(R.id.logIn_button_LogIn)
        var logIn_goBack_button : Button = findViewById(R.id.goBack_button)
        var logIn_email : EditText = findViewById(R.id.email_LogIn)
        var logIn_password : EditText = findViewById(R.id.password_LogIn)

        logIn_button.setOnClickListener {
            val email = logIn_email.text.toString()
            val password = logIn_password.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "User ${task.result?.user?.uid} logged in successfully")
                        val intent = Intent(this, NewMessage::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)

                        val user = auth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "Log in failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        logIn_goBack_button.setOnClickListener{
            finish()
        }
    }
}