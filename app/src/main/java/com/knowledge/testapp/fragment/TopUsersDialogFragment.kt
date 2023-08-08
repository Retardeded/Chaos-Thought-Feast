package com.knowledge.testapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.knowledge.testapp.R
import com.knowledge.testapp.adapters.TopUsersAdapter
import com.knowledge.testapp.data.User

import androidx.fragment.app.DialogFragment
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.data.GameMode


class TopUsersDialogFragment(private val tableName: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_top_users, container, false)

        val databaseReference = FirebaseDatabase.getInstance().getReference(tableName)

        val linearLayout = rootView.findViewById<LinearLayout>(R.id.ll_top_users_dialog)

        val titleTextViewGameMode = rootView.findViewById<TextView>(R.id.tv_top_users_game_mode)
        // Set background drawable based on tableName
        when (tableName) {
            QuizValues.topUsers_FIND_YOUR_LIKINGS -> {
                linearLayout.setBackgroundResource(R.drawable.findyourlikings)
                titleTextViewGameMode.text = GameMode.FIND_YOUR_LIKINGS.toString().replace("_"," ")
            }

            QuizValues.topUsers_LIKING_SPECTRUM_JOURNEY -> {
                linearLayout.setBackgroundResource(R.drawable.likingspecturmjourney2)
                titleTextViewGameMode.text = GameMode.LIKING_SPECTRUM_JOURNEY.toString().replace("_"," ")

            }
            // Add more cases as needed
        }

        databaseReference.orderByChild("currentScore").limitToLast(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val topUsersList = ArrayList<User>()

                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.let { topUsersList.add(it) }
                    }

                    // Sort the topUsersList in descending order based on currentScore
                    val sortedTopUsersList = topUsersList.sortedByDescending { it.currentScore }

                    // Now you have the top 10 users with the highest scores in the sortedTopUsersList
                    // You can use this list to display the data in your fragment's UI

                    // Assuming you have a RecyclerView with the id "rvTopUsers" in your layout,
                    // create an instance of the RecyclerView and set its layout manager and adapter.
                    val recyclerView =
                        rootView.findViewById<RecyclerView>(R.id.rvTopUsers)
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = TopUsersAdapter(sortedTopUsersList)

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