package com.knowledge.testapp.localdb

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns._ID

class UserPathDbHelper(contex: Context) : SQLiteOpenHelper(contex, DB_NAME, null, DB_VER) {

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        createTable(sqLiteDatabase)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        dropTable(sqLiteDatabase)
        createTable(sqLiteDatabase)
    }

    private fun createTable(database: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_USER (
                $_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $USER_ID TEXT NOT NULL,
                $STARTTITLE TEXT NOT NULL,
                $GOALTITLE TEXT NOT NULL,
                $PATH TEXT NOT NULL,
                $PATHLENGTH INTEGER NOT NULL,
                $SUCCESS INTEGER NOT NULL);
        """.trimIndent()

        database.execSQL(createTableQuery)
    }


    private fun dropTable(database: SQLiteDatabase) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_USER"
        database.execSQL(dropTableQuery)
    }

    companion object {
        private const val DB_NAME = "dbuser"
        private const val DB_VER = 8

        const val TABLE_USER = "user"
        const val USER_ID = "user_id"
        const val STARTTITLE = "start_title"
        const val GOALTITLE = "goal_title"
        const val PATH = "path"
        const val PATHLENGTH = "path_length"
        const val SUCCESS = "success"
    }
}