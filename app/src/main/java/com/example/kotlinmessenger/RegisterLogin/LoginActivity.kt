package com.example.kotlinmessenger.RegisterLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val btnLogin : Button = findViewById(R.id.btn_login)
        val haveAccount : TextView = findViewById(R.id.tv_have_account)

        btnLogin.setOnClickListener {
            val loginEmail = findViewById<EditText>(R.id.ed_email_login).text.toString()
            val loginPass = findViewById<EditText>(R.id.ed_password_login).text.toString()

            Log.d("Login ", "Attempted with email $loginEmail")
            Log.d("Login ", "Attempted with password $loginPass")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmail,loginPass)
                .addOnCompleteListener {
                    if(!it.isSuccessful) {
                        Log.d("firebase", "Login failed")
                        return@addOnCompleteListener
                    }

                    //else if successful
                    Log.d("firebase", "Logged In Successfully")
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("firebase", "Failed to Login  ${it.message}")
                    Toast.makeText(this, "Failed to login ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        haveAccount.setOnClickListener {
            finish()
        }



    }


}