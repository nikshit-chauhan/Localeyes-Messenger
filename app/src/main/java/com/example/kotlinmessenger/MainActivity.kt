package com.example.kotlinmessenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnRegister : Button = findViewById(R.id.btn_register)
        val haveAccount : TextView = findViewById(R.id.tv_have_account)

        btnRegister.setOnClickListener{
            val email = findViewById<EditText>(R.id.ed_email_register).text.toString()
            val password = findViewById<EditText>(R.id.ed_password_register).text.toString()

            Log.d("Main Activity", "Email.is: $email")
            Log.d("Main Activity", "Password: $password")
        }
        haveAccount.setOnClickListener{
            Log.d("Main Activity", "clicked on already have an account!!")

            //launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }
}