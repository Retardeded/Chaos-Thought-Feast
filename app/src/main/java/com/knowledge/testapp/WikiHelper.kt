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
import android.provider.BaseColumns._ID
import java.util.ArrayList

class WikiHelper(private val context: Context) {
    private var dbWikiHelper: DbWikiHelper = DbWikiHelper(context)
    private var database: SQLiteDatabase = dbWikiHelper.writableDatabase

    fun getSuccessfulPaths(): ArrayList<PathItem> {
        val cursor = database.query(TABLE_USER, null, "$SUCCESS > 0", null, null, null, null)
        val arrayList = ArrayList<PathItem>()

        while (cursor.moveToNext()) {
            val wikiPath = PathItem().apply {
                _id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                titleStart = cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                titleGoal = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                path = cursor.getString(cursor.getColumnIndexOrThrow(PATH))
                pathLength = cursor.getInt(cursor.getColumnIndexOrThrow(PATHLENGTH))
                success = cursor.getInt(cursor.getColumnIndexOrThrow(SUCCESS))
            }

            arrayList.add(wikiPath)
        }

        cursor.close()
        return arrayList
    }

    fun getUnsuccessfulPaths(): ArrayList<PathItem> {
        val cursor = database.query(TABLE_USER, null, "$SUCCESS < 0", null, null, null, null)
        val arrayList = ArrayList<PathItem>()

        while (cursor.moveToNext()) {
            val wikiPath = PathItem().apply {
                _id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                titleStart = cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                titleGoal = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                path = cursor.getString(cursor.getColumnIndexOrThrow(PATH))
                pathLength = cursor.getInt(cursor.getColumnIndexOrThrow(PATHLENGTH))
                success = cursor.getInt(cursor.getColumnIndexOrThrow(SUCCESS))
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