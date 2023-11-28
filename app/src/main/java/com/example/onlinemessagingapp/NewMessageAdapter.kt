package com.example.onlinemessagingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class NewMessageAdapter(private val userList: List<User>, private val onItemClicked: (item : User, position: Int)-> Unit) :
    RecyclerView.Adapter<NewMessageAdapter.ViewHolder>(){


        inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            val pfpImage: ImageView = itemView.findViewById(R.id.pfpImageView)
            val usernameView: TextView = itemView.findViewById(R.id.usernameTextView)

            init {
                itemView.setOnClickListener(this)
            }
            override fun onClick(v: View) {
                val item : User = userList[position]
                onItemClicked(item, position)
            }
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.user_item_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val user : User = userList[position]
        Picasso.get().load(user.pfpUrl).into(viewHolder.pfpImage)
        viewHolder.usernameView.text = user.username
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
