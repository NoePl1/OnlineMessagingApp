package com.example.onlinemessagingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ChatLogAdapter(val dataSet: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val toId = 1 // Id of holder ToMessage
    private val fromId = -1 // Id of holder FromMessage

    override fun getItemViewType(position: Int): Int {

        val msg = dataSet[position]

        if (msg.toId == ChatLog.user) {
            return fromId // receive message
        } else {
            return toId // send message
        }
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        val context = parent.context

        if (viewType == toId) {
            itemView = LayoutInflater.from(context).inflate(R.layout.chatlog_to_row, parent, false)
            return ToMessage(itemView)
        } else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.chatlog_from_row, parent, false)
            return FromMessage(itemView)
        }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val message = dataSet[position]

            if (holder.itemViewType == toId) {
                (holder as ToMessage).bind(message)
            } else {
                (holder as FromMessage).bind(message)
            }
        }

        override fun getItemCount() = dataSet.size
}