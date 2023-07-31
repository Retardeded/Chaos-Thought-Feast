package com.knowledge.testapp.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.R
import com.knowledge.testapp.adapters.PathDataAdapter
import com.knowledge.testapp.data.PathRecord

class PathsDialogFragment(private val paths: ArrayList<PathRecord>, private val isWinning: Boolean) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_paths_dialog, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.titleTextView)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PathDataAdapter(paths)
        recyclerView.adapter = adapter


        recyclerView.adapter = adapter

        if (isWinning) {
            tvTitle.text = "Winning Paths"
        } else {
            tvTitle.text = "Losing Paths"
        }

        builder.setView(dialogView)

        val closeButton = dialogView.findViewById<Button>(R.id.btn_close_dialog)
        closeButton.setOnClickListener {
            dismiss() // Dismiss the dialog when the close button is clicked
        }

        return builder.create()
    }
}
