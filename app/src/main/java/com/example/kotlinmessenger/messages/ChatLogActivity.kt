package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.modules.ChatMessage
import com.example.kotlinmessenger.modules.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class ChatLogActivity : AppCompatActivity() {

    companion object{
        const val TAG = "ChatLog"
    }
    private val adapter = GroupieAdapter()
    var toUser : User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        findViewById<RecyclerView>(R.id.rv_Chat_Log).adapter = adapter

        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
        val btnSend :Button = findViewById(R.id.btn_send_chat_Log)

        supportActionBar?.title = toUser?.username

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
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    }else{
                            adapter.add(ChatToItem(chatMessage.text, toUser!!))

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
        toUser = intent.getParcelableExtra(NewMessageActivity.USER_KEY)
        val toId = toUser?.uid
        if(fromId == null) return
//        val ref = FirebaseDatabase.getInstance().getReference("/Messages").push()
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val chatMessage = ChatMessage(ref.key!!,messageText, fromId, toId!!, System.currentTimeMillis() / 1000)
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Message sent to database ${ref.key}")
            }
    }
}
class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_msg_received).text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_CurrentUser)
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.current_user
    }

}
class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_msg_sent).text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_otherUser)
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.other_user
    }

}