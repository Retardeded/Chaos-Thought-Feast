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
import com.knowledge.testapp.data.PathItem
import com.knowledge.testapp.data.WorldRecord
import java.util.ArrayList

class WikiHelper(private val context: Context) {
    private var dbWikiHelper: DbWikiHelper = DbWikiHelper(context)
    private var database: SQLiteDatabase = dbWikiHelper.writableDatabase

    fun getSuccessfulPaths(): ArrayList<WorldRecord> {
        val cursor = database.query(TABLE_USER, null, "$SUCCESS > 0", null, null, null, null)
        val arrayList = ArrayList<WorldRecord>()

        while (cursor.moveToNext()) {
            val wikiPath = WorldRecord().apply {
                startingConcept = cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                goalConcept = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                path = cursor.getString(cursor.getColumnIndexOrThrow(PATH)).split("->").toMutableList() as ArrayList<String>
                steps = cursor.getInt(cursor.getColumnIndexOrThrow(PATHLENGTH))
                win = cursor.getInt(cursor.getColumnIndexOrThrow(SUCCESS)) > 0
            }

            arrayList.add(wikiPath)
        }

        cursor.close()
        return arrayList
    }

    fun getUnsuccessfulPaths(): ArrayList<WorldRecord> {
        val cursor = database.query(TABLE_USER, null, "$SUCCESS < 0", null, null, null, null)
        val arrayList = ArrayList<WorldRecord>()

        while (cursor.moveToNext()) {
            val wikiPath = WorldRecord().apply {
                startingConcept = cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                goalConcept = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                path = cursor.getString(cursor.getColumnIndexOrThrow(PATH)).split("->").toMutableList() as ArrayList<String>
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

    fun insert(wikiPath: PathItem): Long {
        val values = ContentValues().apply {
            put(STARTTITLE, wikiPath.titleStart)
            put(GOALTITLE, wikiPath.titleGoal)
            put(PATH, wikiPath.path)
            put(PATHLENGTH, wikiPath.pathLength)
            put(SUCCESS, wikiPath.success)
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