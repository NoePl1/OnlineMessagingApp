package com.example.onlinemessagingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatLog : AppCompatActivity() {
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val userUid = intent.getStringExtra(NewMessage.UID_KEY)
        Log.d("TAG", "selected uid : $userUid")
        val username = intent.getStringExtra(NewMessage.USERNAME_KEY)
        Log.d("TAG", "selected username : $username")
        val pfpUrl = intent.getStringExtra(NewMessage.PFPURL_KEY)!!
        Log.d("TAG", "selected pfp : $pfpUrl")

        var user = User(userUid!!, username!!, pfpUrl!!)
        Log.d("TAG", "user : ${user.username}")

        supportActionBar?.title = user.username

        val sendButton = findViewById<Button>(R.id.send_button_chatlog)

        sendButton.setOnClickListener{
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
            sendMessage()
        }

        var messages = ArrayList<Message>()


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_chatlog)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ChatLogAdapter(messages) // val adapter = ChatLogAdapter(messages, pfpUrl)
        recyclerView.adapter = adapter

        //Listen for new messages
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid
        val ref = db.collection("/user-messages/$fromId/$toId")

        ref.orderBy("timestamp").addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                for (text in snapshot!!.documentChanges) {
                    var textmsg = text.document.data.get("text")
                    var idmsg = text.document.data.get("id")
                    var fromidmsg = text.document.data.get("fromId")
                    var toidmsg = text.document.data.get("toId")
                    var timemsg = text.document.data.get("timestamp")

                    if (textmsg != null) {
                        var msg = Message(
                            idmsg.toString(),
                            textmsg.toString(),
                            fromidmsg.toString(),
                            toidmsg.toString(),
                            timemsg as Long
                        )
                        messages.add(msg)
                    }
                }

                adapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(messages.size - 1)
            }
            else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun sendMessage() {
        val userUid = intent.getStringExtra(NewMessage.UID_KEY)
        val username = intent.getStringExtra(NewMessage.USERNAME_KEY)
        val pfpUrl = intent.getStringExtra(NewMessage.PFPURL_KEY)
        var user = User(userUid!!, username!!, pfpUrl!!)

        val fromId = FirebaseAuth.getInstance().uid
        if (fromId == null)
            return

        val toId = user.uid
        val timestamp = System.currentTimeMillis() / 1000

        val edittext_chatlog = findViewById<EditText>(R.id.edittext_chatlog)
        val text = edittext_chatlog.text.toString()

        val ref = db.collection("/user-messages/$fromId/$toId")

        val toRef = db.collection("/user-messages/$toId/$fromId")


        val docRef = ref.document().id

        val message = Message(docRef,text, fromId,toId, timestamp )
        ref.add(message)
            .addOnSuccessListener {
                Log.d("TAG", "Saved our chat message : $ref")
                edittext_chatlog.text.clear()
            }
        toRef.add(message)

        val latestMessageRef = db.collection("/latest-messages/$fromId/$toId")
        latestMessageRef.orderBy("timestamp", Query.Direction.DESCENDING).limit(1)
        latestMessageRef.add(message)
    }

    companion object{val user = FirebaseAuth.getInstance().uid}
}