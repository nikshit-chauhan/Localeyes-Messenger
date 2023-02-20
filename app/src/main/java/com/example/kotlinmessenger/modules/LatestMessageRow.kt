package com.example.kotlinmessenger.modules

import android.widget.TextView
import com.example.kotlinmessenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class LatestMessageRow(private val chatMessage: ChatMessage): Item<GroupieViewHolder>(){
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_latest_message).text = chatMessage.text

        val chatPartnerId : String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        }else{
            chatPartnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/user/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.findViewById<TextView>(R.id.tv_latest_message_User).text = chatPartnerUser?.username
                val targetUserImage = viewHolder.itemView.findViewById<CircleImageView>(R.id.iv_latest_message)
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetUserImage)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}