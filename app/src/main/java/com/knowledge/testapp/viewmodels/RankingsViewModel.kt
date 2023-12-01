package com.knowledge.testapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.knowledge.testapp.data.PathRecord
import com.knowledge.testapp.data.User

class RankingsViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseDatabaseRef = FirebaseDatabase.getInstance()

    companion object {
        const val KEY_CURRENT_SCORE = "currentScore"
    }

    fun fetchTopUsers(tableName: String, limit: Int = 10, onSuccess: (List<User>) -> Unit, onFailure: (DatabaseError) -> Unit) {
        val query = firebaseDatabaseRef.getReference(tableName)
            .orderByChild(KEY_CURRENT_SCORE).limitToLast(limit)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                    .sortedByDescending { it.currentScore }
                onSuccess(users)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }


    fun fetchUserRecords(tableName: String, userSanitizedEmail: String, onSuccess: (List<PathRecord>) -> Unit, onFailure: (DatabaseError) -> Unit) {
        val query = firebaseDatabaseRef.getReference(tableName)
            .orderByKey().startAt(userSanitizedEmail).endAt(userSanitizedEmail + "\uf8ff")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue(PathRecord::class.java)
                }
                onSuccess(records)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }
}