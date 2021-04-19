package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PreviousAttempts : AppCompatActivity() {
    internal var userHelper: UserHelper? = null
    internal var recyclerView: RecyclerView? = null
    internal var wikiPaths: ArrayList<PathItem>? = null
    internal var adapter: RecyclerviewAdapter? = null
    private lateinit var buttonFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_attempts)
        buttonFinish = findViewById(R.id.btn_try_again)
        recyclerView = findViewById<View>(R.id.recyler_paths) as RecyclerView

        showList()

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showList() {
        userHelper = UserHelper(this)
        adapter = RecyclerviewAdapter(this)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
        userHelper!!.open()
        wikiPaths = userHelper!!.user
        userHelper!!.close()
        adapter!!.addItem(wikiPaths!!)

    }
}