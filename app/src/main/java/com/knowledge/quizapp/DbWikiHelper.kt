package com.knowledge.quizapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.provider.BaseColumns._ID
import com.knowledge.quizapp.WikiPath.TABLE_USER

import com.knowledge.quizapp.WikiPath.UserColumns.Companion.STARTTITLE
import com.knowledge.quizapp.WikiPath.UserColumns.Companion.GOALTITLE
import com.knowledge.quizapp.WikiPath.UserColumns.Companion.PATH

class DbWikiHelper(c: Context) : SQLiteOpenHelper(c, DB_NAME, null, DB_VER) {


    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USER)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        onCreate(sqLiteDatabase)
    }

    companion object {
        private val DB_NAME = "dbuser"
        private val DB_VER = 1

        var CREATE_TABLE_USER = "create table " + TABLE_USER +
                " (" + _ID + " integer primary key autoincrement, " +
                STARTTITLE + " text not null, " +
                GOALTITLE + " text not null, " +
                PATH + " text not null);"
    }
}