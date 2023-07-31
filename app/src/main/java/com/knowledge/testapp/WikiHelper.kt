package com.knowledge.testapp

import DbWikiHelper
import DbWikiHelper.Companion.GOALTITLE
import DbWikiHelper.Companion.PATH
import DbWikiHelper.Companion.PATHLENGTH
import DbWikiHelper.Companion.STARTTITLE
import DbWikiHelper.Companion.SUCCESS
import DbWikiHelper.Companion.TABLE_USER
import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.knowledge.testapp.data.PathRecord
import java.util.ArrayList

class WikiHelper(private val context: Context) {
    private var dbWikiHelper: DbWikiHelper = DbWikiHelper(context)
    private var database: SQLiteDatabase = dbWikiHelper.writableDatabase

    fun getSuccessfulPaths(): ArrayList<PathRecord> {
        val cursor = database.query(TABLE_USER, null, "$SUCCESS > 0", null, null, null, null)
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

    fun getUnsuccessfulPaths(): ArrayList<PathRecord> {
        val cursor = database.query(TABLE_USER, null, "$SUCCESS == 0", null, null, null, null)
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
    fun open(): WikiHelper {
        database = dbWikiHelper.writableDatabase
        return this
    }

    fun close() {
        dbWikiHelper.close()
    }

    fun insert(wikiPath: PathRecord): Long {
        val values = ContentValues().apply {
            put(STARTTITLE, wikiPath.startingConcept)
            put(GOALTITLE, wikiPath.goalConcept)
            val pathJson = Gson().toJson(wikiPath.path)
            put(PATH, pathJson)
            put(PATHLENGTH, wikiPath.steps)
            put(SUCCESS, wikiPath.win)
        }
        return database.insert(TABLE_USER, null, values)
    }

    fun clearTableUser() {
        try {
            // Execute the SQL command to delete all rows from TABLE_USER
            database.execSQL("DELETE FROM $TABLE_USER")
        } catch (e: SQLException) {
            e.printStackTrace()
        }
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