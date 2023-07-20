package com.knowledge.testapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PreviousAttempts : AppCompatActivity() {
    private lateinit var wikiHelper: WikiHelper
    private lateinit var recyclerViewWinningPaths: RecyclerView
    private lateinit var adapterWinningPaths: RecyclerviewAdapter
    private lateinit var recyclerViewLosingPaths: RecyclerView
    private lateinit var adapterLosingPaths: RecyclerviewAdapter
    private lateinit var buttonFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_attempts)
        buttonFinish = findViewById(R.id.btn_try_again)
        recyclerViewWinningPaths = findViewById<View>(R.id.recycler_win_paths) as RecyclerView
        recyclerViewLosingPaths = findViewById<View>(R.id.recycler_lose_paths) as RecyclerView
        setupRecyclerViews()

        showList()

        buttonFinish.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupRecyclerViews() {
        wikiHelper = WikiHelper(this)

        adapterWinningPaths = RecyclerviewAdapter(this)
        recyclerViewWinningPaths.layoutManager = LinearLayoutManager(this)
        recyclerViewWinningPaths.adapter = adapterWinningPaths

        adapterLosingPaths = RecyclerviewAdapter(this)
        recyclerViewLosingPaths.layoutManager = LinearLayoutManager(this)
        recyclerViewLosingPaths.adapter = adapterLosingPaths
    }

    private fun showList() {
        adapterWinningPaths.addItem(wikiHelper.getSuccessfulPaths())
        adapterLosingPaths.addItem(wikiHelper.getUnsuccessfulPaths())
    }

    fun clearPaths(view: View) {
        wikiHelper.clearTableUser()
        adapterWinningPaths.clearItems()
        adapterLosingPaths.clearItems()
        showList() // After clearing, refresh the data
    }

    fun togglePaths(view: View) {
        val recyclerViewWin = findViewById<RecyclerView>(R.id.recycler_win_paths)
        val recyclerViewLose = findViewById<RecyclerView>(R.id.recycler_lose_paths)
        val tvPaths = findViewById<TextView>(R.id.tv_successful_paths)
        val btnTogglePaths = findViewById<Button>(R.id.btn_toggle_paths)

        if (recyclerViewWin.visibility == View.VISIBLE) {
            recyclerViewWin.visibility = View.GONE
            recyclerViewLose.visibility = View.VISIBLE
            tvPaths.text = "Previous unsuccessful attempts"
            btnTogglePaths.text = "Show wins"
        } else {
            recyclerViewWin.visibility = View.VISIBLE
            recyclerViewLose.visibility = View.GONE
            tvPaths.text = "Previous successful attempts"
            btnTogglePaths.text = "Show loses"
        }
    }
}