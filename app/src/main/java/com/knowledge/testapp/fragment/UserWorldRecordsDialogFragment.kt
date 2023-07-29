package com.knowledge.testapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.knowledge.testapp.PathDataAdapter
import com.knowledge.testapp.R
import com.knowledge.testapp.data.WorldRecord

class UserWorldRecordsDialogFragment(private val userSanitizedEmail: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_world_records_dialog, container, false)

        val databaseReference = FirebaseDatabase.getInstance().getReference("worldRecords")

        // Query to get all world records for the logged-in user
        databaseReference.orderByKey().startAt(userSanitizedEmail).endAt(userSanitizedEmail + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userWorldRecordsList = ArrayList<WorldRecord>()

                    for (recordSnapshot in snapshot.children) {
                        val startingConcept = recordSnapshot.child("startingConcept").getValue(String::class.java)
                        val goalConcept = recordSnapshot.child("goalConcept").getValue(String::class.java)
                        val steps = recordSnapshot.child("steps").getValue(Int::class.java)
                        val win = recordSnapshot.child("win").getValue(Boolean::class.java)

                        // Get the path as an ArrayList<String>
                        val pathList = recordSnapshot.child("path").getValue(object : GenericTypeIndicator<ArrayList<String>>() {})

                        val worldRecord = WorldRecord(startingConcept ?: "", goalConcept ?: "", pathList ?: ArrayList(), steps ?: 0, win ?: false)
                        userWorldRecordsList.add(worldRecord)
                    }

                    // Now you have the world records held by the logged-in user in the userWorldRecordsList
                    // You can use this list to display the data in your fragment's UI

                    // Assuming you have a RecyclerView with the id "rvUserWorldRecords" in your layout,
                    // create an instance of the RecyclerView and set its layout manager and adapter.
                    val recyclerView = rootView.findViewById<RecyclerView>(R.id.rvUserWorldRecords)
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = PathDataAdapter(userWorldRecordsList)

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