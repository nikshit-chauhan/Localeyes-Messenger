package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.NewMessageActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.modules.ChatMessage
import com.example.kotlinmessenger.modules.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.lang.ref.Reference

class ChatLogActivity : AppCompatActivity() {

    companion object{
        const val TAG = "ChatLog"
    }
    private val adapter = GroupieAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.rv_Chat_Log).adapter = adapter

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val btnSend :Button = findViewById(R.id.btn_send_chat_Log)

        supportActionBar?.title = user?.username

//        setupData()
        listenForMessages()

        btnSend.setOnClickListener {
            Log.d(TAG, "send button pressed")
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/Messages")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null){
                    Log.d(TAG, chatMessage.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }


    private fun performSendMessage() {
        val messageText = findViewById<EditText>(R.id.et_type_chat_log).text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        if(fromId == null) return
        val ref = FirebaseDatabase.getInstance().getReference("/Messages").push()

        val chatMessage = ChatMessage(ref.key!!,messageText, fromId, toId!!, System.currentTimeMillis() / 1000)
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Message sent to database ${ref.key}")
            }
    }

    private fun setupData() {
        val adapter = GroupieAdapter()
        adapter.add(ChatToItem("m gooddddsadasdaxdcajkbsxcabxckabsxiansx"))
        adapter.add(ChatFromItem("helloe marty...hwo are uppjoiandanxa"))
        adapter.add(ChatToItem("m gooddddsadasdaxdcajkbsxcabxckabsxiansx"))
        adapter.add(ChatFromItem("helloe marty...hwo are uppjoiandanxa"))

        val rvChatLog = findViewById<RecyclerView>(R.id.rv_Chat_Log)
        rvChatLog.adapter = adapter
    }
}
class ChatFromItem(val text: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_msg_received).text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}
class ChatToItem(val text: String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_msg_sent).text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}