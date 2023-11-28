package com.example.onlinemessagingapp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ToMessage(val sentView: View) : RecyclerView.ViewHolder(sentView) {
    var message = sentView.findViewById<TextView>(R.id.to_message)

    fun bind(msg: Message) {
        message.text = msg.text
    }
}

