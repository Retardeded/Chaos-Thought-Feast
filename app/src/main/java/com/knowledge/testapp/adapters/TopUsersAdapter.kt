package com.knowledge.testapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.R
import com.knowledge.testapp.data.User

class TopUsersAdapter(private val topUsersList: List<User>) : RecyclerView.Adapter<TopUsersAdapter.TopUsersViewHolder>() {

    inner class TopUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userTextView: TextView = itemView.findViewById(R.id.tvUsername)
        val scoreTextView: TextView = itemView.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return TopUsersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TopUsersViewHolder, position: Int) {
        val user = topUsersList[position]
        holder.userTextView.text = user.username
        holder.scoreTextView.text = user.currentScore.toString()
    }

    override fun getItemCount(): Int {
        return topUsersList.size
    }
}