package com.example.kotlinmessenger.RegisterLogin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.modules.User
import com.example.kotlinmessenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val btnRegister : Button = findViewById(R.id.btn_register)
        val haveAccount : TextView = findViewById(R.id.tv_have_account)
        val btnImage : Button = findViewById(R.id.btn_Image)

        btnRegister.setOnClickListener{
            performRegister()
        }
        haveAccount.setOnClickListener{
            Log.d("Main Activity", "clicked on already have an account!!")

            //launch login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        btnImage.setOnClickListener {
            Log.d("Main Activity", "Try to show photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityIfNeeded(intent, 0)
        }
    }
    var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("register", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val photo = findViewById<CircleImageView>(R.id.photo_imageView_register).setImageBitmap(bitmap)
            findViewById<Button>(R.id.btn_Image).alpha = 0f
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
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Main Activity", "Email.is: $email")
                Log.d("Main Activity", "Password: $password")
                Log.d("firebase", "Failed to create User ${it.message}")
                Toast.makeText(this, "Failed to create User ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("register", "successfully upload image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("register", "File location $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("register Activity", "image not uploaded to firebase")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl : String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val username = findViewById<EditText>(R.id.ed_userName_register)
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")

        val user = User(uid, username.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("register Activity", "Finally we saved the user to firebase database")

                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("register Activity", "user not saved in firebase database")
            }
    }

}
