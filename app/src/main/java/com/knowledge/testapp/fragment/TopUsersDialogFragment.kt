package com.knowledge.testapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.knowledge.testapp.R
import com.knowledge.testapp.adapters.TopUsersAdapter
import com.knowledge.testapp.data.User

import androidx.fragment.app.DialogFragment


class TopUsersDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_top_users, container, false)

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Query to get the top 10 users with the highest scores
        databaseReference.orderByChild("currentScore").limitToLast(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val topUsersList = ArrayList<User>()

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { topUsersList.add(it) }
                    }

                    // Now you have the top 10 users with the highest scores in the topUsersList
                    // You can use this list to display the data in your fragment's UI

                    // Assuming you have a RecyclerView with the id "rvTopUsers" in your layout,
                    // create an instance of the RecyclerView and set its layout manager and adapter.
                    val recyclerView =
                        rootView.findViewById<RecyclerView>(R.id.rvTopUsers)
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = TopUsersAdapter(topUsersList)

                    // Get the close button and set the click listener to dismiss the dialog
                    val closeButton = rootView.findViewById<Button>(R.id.btn_close_dialog)
                    closeButton.setOnClickListener {
                        dismiss()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error, if any
                }
            })

        return rootView
    }
}