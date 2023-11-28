package com.example.onlinemessagingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewMessage : AppCompatActivity() {
    private lateinit var username: String
    private lateinit var uid: String
    private lateinit var pfpImageUrl: String
    private lateinit var userData: User
    private lateinit var adapter: NewMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select a user"

        val db = Firebase.firestore

        val uidAuth = FirebaseAuth.getInstance().uid
        if (uidAuth == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


        var userList: MutableList<User> = ArrayList()

        val ref = db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                Log.d("TAG", "inside users")
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    username = document.getString("username").toString()
                    uid = document.getString("uid").toString()
                    pfpImageUrl = document.getString("pfpUrl").toString()
                    Log.d("TAG", "username: $username")
                    Log.d("TAG", "uid: $uid")
                    Log.d("TAG", "pfpImageUrl: $pfpImageUrl")

                    userData = User(uid, username, pfpImageUrl)
                    Log.d("TAG", "userData = $uid, $username, $pfpImageUrl")
                    userList.add(userData)
                    Log.d("TAG", "userList:${userList[0]}")
                }
                val newMessageRecyclerview = findViewById<RecyclerView>(R.id.newMessageRecyclerView)
                newMessageRecyclerview.layoutManager = LinearLayoutManager(this)
                adapter = NewMessageAdapter(userList){
                        item, position -> onListItemClick(item, position)
                }
                newMessageRecyclerview.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "Error getting documents: ", exception)
            }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu )
        return super.onCreateOptionsMenu(menu)
    }

    companion object{
        val USERNAME_KEY = "USERNAME_KEY"
        val UID_KEY = "UID_KEY"
        val PFPURL_KEY = "PFPURL_KEY"
    }
     fun onListItemClick(item : User, position : Int){
         Toast.makeText(this, "item at pos $position: ${item.username} clicked", Toast.LENGTH_SHORT).show()
         val intent = Intent(this@NewMessage, ChatLog::class.java)
         intent.putExtra(USERNAME_KEY, item.username)
         intent.putExtra(UID_KEY, item.uid)
         intent.putExtra(PFPURL_KEY, item.pfpUrl)
         startActivity(intent)
     }
}

