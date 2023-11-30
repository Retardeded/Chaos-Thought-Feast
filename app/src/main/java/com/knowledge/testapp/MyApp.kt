package com.knowledge.testapp

import android.app.Application
import com.knowledge.testapp.localdb.UserPathDbManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UserPathDbManager.getInstance(this).open()
    }
}