package com.example.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val email : String? = null
    private val password : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val btnRegister : Button = findViewById(R.id.btn_register)
        val haveAccount : TextView = findViewById(R.id.tv_have_account)

        btnRegister.setOnClickListener{
            performRegister()
        }
        haveAccount.setOnClickListener{
            Log.d("Main Activity", "clicked on already have an account!!")

            //launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val email = findViewById<EditText>(R.id.ed_email_register).text.toString()
        val password = findViewById<EditText>(R.id.ed_password_register).text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email /Password", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("Main Activity", "Email.is: $email")
        Log.d("Main Activity", "Password: $password")

        //Firebase Authentication to create new User with email and password

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) {
                    Log.d("firebase", "User NOt created")
                    return@addOnCompleteListener
                }

                //else if successful
                Log.d("firebase", "User created with uid ${it.result.user?.uid}")
            }
            .addOnFailureListener {
                Log.d("firebase", "Failed to create User ${it.message}")
                Toast.makeText(this, "Failed to create User ${it.message}", Toast.LENGTH_LONG).show()
            }
    }


}