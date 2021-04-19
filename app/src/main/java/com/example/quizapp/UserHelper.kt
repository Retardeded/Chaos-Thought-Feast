package com.example.quizapp

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase


import java.util.ArrayList

import android.provider.BaseColumns._ID
import com.example.quizapp.WikiPath.TABLE_USER
import com.example.quizapp.WikiPath.UserColumns.Companion.STARTTITLE
import com.example.quizapp.WikiPath.UserColumns.Companion.GOALTITLE
import com.example.quizapp.WikiPath.UserColumns.Companion.PATH

/**
 * Created by humaira on 4/9/2018.
 */

class UserHelper(private val c: Context) {
    private var dbWikiHelper: DbWikiHelper? = null
    private var database: SQLiteDatabase? = null
    val user: ArrayList<PathItem>
        get() {
            val result = ""
            val cursor = database!!.query(TABLE_USER, null, null, null, null, null, _ID + " ASC", null)
            cursor.moveToFirst()
            val arrayList = ArrayList<PathItem>()
            var wikiPath: PathItem
            if (cursor.count > 0) {
                do {
                    wikiPath = PathItem()
                    wikiPath._id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                    wikiPath.titleStart= cursor.getString(cursor.getColumnIndexOrThrow(STARTTITLE))
                    wikiPath.titleGoal = cursor.getString(cursor.getColumnIndexOrThrow(GOALTITLE))
                    wikiPath.path = cursor.getString(cursor.getColumnIndexOrThrow(PATH))

                    arrayList.add(wikiPath)
                    cursor.moveToNext()
                } while (!cursor.isAfterLast)
            }
            cursor.close()
            return arrayList
        }

    @Throws(SQLException::class)
    fun open(): UserHelper {
        dbWikiHelper = DbWikiHelper(c)
        database = dbWikiHelper!!.writableDatabase
        return this
    }

    fun close() {
        dbWikiHelper!!.close()
    }

    fun insert(wikiPath: PathItem): Long {
        val values = ContentValues()
        values.put(STARTTITLE, wikiPath.titleStart)
        values.put(GOALTITLE, wikiPath.titleGoal)
        values.put(PATH, wikiPath.path)
        return database?.insert(TABLE_USER, null, values)!!
    }

    fun beginTransaction() {
        database!!.beginTransaction()
    }

    fun setTransactionSuccess() {
        database!!.setTransactionSuccessful()
    }

    fun endTransaction() {
        database!!.endTransaction()
    }
}