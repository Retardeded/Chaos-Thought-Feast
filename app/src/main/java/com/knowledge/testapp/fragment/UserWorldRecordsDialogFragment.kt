package com.knowledge.testapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.adapters.PathDataAdapter
import com.knowledge.testapp.R
import com.knowledge.testapp.data.GameMode
import com.knowledge.testapp.data.PathRecord

class UserWorldRecordsDialogFragment(private val userSanitizedEmail: String, private val tableName: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_world_records_dialog, container, false)

        // Find the LinearLayout with the background
        val linearLayout = rootView.findViewById<LinearLayout>(R.id.ll_user_records_dialog)

        val titleTextViewRecord = rootView.findViewById<TextView>(R.id.titleTextViewWorldRecords)
        val titleTextViewUser = rootView.findViewById<TextView>(R.id.titleTextViewUserName)
        // Set background drawable based on tableName
        when (tableName) {
            QuizValues.worldRecords_FIND_YOUR_LIKINGS -> {
                linearLayout.setBackgroundResource(R.drawable.findyourlikings)
                titleTextViewRecord.text = GameMode.FIND_YOUR_LIKINGS.toString().replace("_"," ")
            }

            QuizValues.worldsRecords_LIKING_SPECTRUM_JOURNEY -> {
                linearLayout.setBackgroundResource(R.drawable.likingspecturmjourney2)
                titleTextViewRecord.text = GameMode.LIKING_SPECTRUM_JOURNEY.toString().replace("_"," ")

            }
            // Add more cases as needed
        }
        titleTextViewUser.text = QuizValues.USER!!.username

        val databaseReference = FirebaseDatabase.getInstance().getReference(tableName)

        // Query to get all world records for the logged-in user
        databaseReference.orderByKey().startAt(userSanitizedEmail).endAt(userSanitizedEmail + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userPathRecordsList = ArrayList<PathRecord>()

                    for (recordSnapshot in snapshot.children) {
                        val startingConcept = recordSnapshot.child("startingConcept").getValue(String::class.java)
                        val goalConcept = recordSnapshot.child("goalConcept").getValue(String::class.java)
                        val steps = recordSnapshot.child("steps").getValue(Int::class.java)
                        val win = recordSnapshot.child("win").getValue(Boolean::class.java)

                        // Get the path as an ArrayList<String>
                        val pathList = recordSnapshot.child("path").getValue(object : GenericTypeIndicator<ArrayList<String>>() {})

                        val pathRecord = PathRecord(startingConcept ?: "", goalConcept ?: "", pathList ?: ArrayList(), steps ?: 0, win ?: false)
                        userPathRecordsList.add(pathRecord)
                    }

                    // Now you have the world records held by the logged-in user in the userWorldRecordsList
                    // You can use this list to display the data in your fragment's UI

                    // Assuming you have a RecyclerView with the id "rvUserWorldRecords" in your layout,
                    // create an instance of the RecyclerView and set its layout manager and adapter.
                    val recyclerView = rootView.findViewById<RecyclerView>(R.id.rvUserWorldRecords)
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = PathDataAdapter(userPathRecordsList)

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