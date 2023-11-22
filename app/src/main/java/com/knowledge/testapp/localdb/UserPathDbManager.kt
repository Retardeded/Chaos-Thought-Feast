package com.knowledge.testapp.localdb

import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.GOALTITLE
import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.PATH
import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.PATHLENGTH
import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.STARTTITLE
import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.SUCCESS
import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.TABLE_USER
import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.knowledge.testapp.data.PathRecord
import com.knowledge.testapp.localdb.UserPathDbHelper.Companion.USER_ID
import java.util.ArrayList

class UserPathDbManager(private val context: Context) {
    private var userPathDbHelper: UserPathDbHelper = UserPathDbHelper(context)
    private var database: SQLiteDatabase = userPathDbHelper.writableDatabase

    fun getPathsForUser(userId: String, success: Boolean): ArrayList<PathRecord> {
        val successValue = if (success) 1 else 0
        val cursor = database.query(
            TABLE_USER,
            null,
            "$USER_ID = ? AND $SUCCESS = ?",
            arrayOf(userId, successValue.toString()),
            null, null, null
        )
        val arrayList = ArrayList<PathRecord>()

        while (cursor.moveToNext()) {
            val wikiPath = PathRecord().apply {
                startingConcept = cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                goalConcept = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                val pathJson = cursor.getString(cursor.getColumnIndexOrThrow(PATH))
                path = Gson().fromJson(pathJson, object : TypeToken<List<String>>() {}.type)
                steps = cursor.getInt(cursor.getColumnIndexOrThrow(PATHLENGTH))
                win = cursor.getInt(cursor.getColumnIndexOrThrow(SUCCESS)) > 0
            }

            arrayList.add(wikiPath)
        }

        cursor.close()

        return arrayList
    }

    @Throws(SQLException::class)
    fun open(): UserPathDbManager {
        database = userPathDbHelper.writableDatabase
        return this
    }

    fun close() {
        userPathDbHelper.close()
    }

    fun insert(userId: String, wikiPath: PathRecord): Long {
        val values = ContentValues().apply {
            put(USER_ID, userId) // Include the userId
            put(STARTTITLE, wikiPath.startingConcept)
            put(GOALTITLE, wikiPath.goalConcept)
            val pathJson = Gson().toJson(wikiPath.path)
            put(PATH, pathJson)
            put(PATHLENGTH, wikiPath.steps)
            put(SUCCESS, wikiPath.win)
        }
        return database.insert(TABLE_USER, null, values)
    }

    fun clearUserData(userId: String) {
        val whereClause = "$USER_ID = ?"
        val whereArgs = arrayOf(userId)
        database.delete(TABLE_USER, whereClause, whereArgs)
    }

    fun beginTransaction() {
        database.beginTransaction()
    }

    fun setTransactionSuccess() {
        database.setTransactionSuccessful()
    }

    fun endTransaction() {
        database.endTransaction()
    }
}