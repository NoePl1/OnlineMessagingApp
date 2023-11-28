package com.example.onlinemessagingapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FromMessage(val receivedView: View) : RecyclerView.ViewHolder(receivedView) {
    var message = receivedView.findViewById<TextView>(R.id.from_message)

    fun bind(msg: Message) {
        message.text=(msg.text)
    }
}