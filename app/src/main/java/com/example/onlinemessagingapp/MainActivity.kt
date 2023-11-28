package com.example.onlinemessagingapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Firebase.firestore
        auth = Firebase.auth

        var registration_signUp : Button = findViewById(R.id.signUp_button)
        var registration_logIn : Button = findViewById(R.id.logIn_button)
        var selectPhoto_button : Button = findViewById(R.id.selectPhoto_button)

        var registration_email : EditText = findViewById(R.id.email_editText_register)
        var registration_password : EditText = findViewById(R.id.password_editText_register)


        //How to Sign-Up :
        registration_signUp.setOnClickListener {
            val email = registration_email.text.toString()
            val password = registration_password.text.toString()

            //check if empty
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter an e-mail and username!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            //Creation of the user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "Successfully created user : ${task.result?.user?.uid}")
                        pfpToDb()
                        val user = auth.currentUser

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("TAG", "Failed to create user", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }
                }
        }

        registration_logIn.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
        }

        selectPhoto_button.setOnClickListener{
            Log.d("TAG", "Opening photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoURI : Uri? = null

    //Updates the photo button to show the selected photo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectPhoto_imageView = findViewById<CircleImageView>(R.id.selectPhoto_imageView)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("TAG", "Photo was updated")

            selectedPhotoURI = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)

            selectPhoto_imageView.setImageBitmap(bitmap)

            var selectPhoto_button : Button = findViewById(R.id.selectPhoto_button)
            selectPhoto_button.alpha = 0f
        }



    }

    private fun pfpToDb(){
        if(selectedPhotoURI == null)
            return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoURI!!).addOnSuccessListener { task ->
            Log.d("TAG", "Image uploaded successfully : ${task.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                userToDb(it.toString())
            }

        }
    }

    private fun userToDb(pfpUrl: String){
        val username = findViewById<EditText>(R.id.username_editText_register).text.toString()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = db.collection("users")

        val user = User(uid, username, pfpUrl)

        ref.add(user)
            .addOnSuccessListener {
                Log.d("TAG", "user saved to firebase db")
                val intent = Intent(this, NewMessage::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}