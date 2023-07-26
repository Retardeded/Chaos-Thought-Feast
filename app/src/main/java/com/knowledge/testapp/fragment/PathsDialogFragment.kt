package com.knowledge.testapp.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.PathItem
import com.knowledge.testapp.R
import com.knowledge.testapp.RecyclerviewAdapter

class PathsDialogFragment(private val paths: ArrayList<PathItem>, private val isWinning: Boolean) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_paths_dialog, null)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecyclerviewAdapter(requireContext())
        adapter.addItem(paths)


        recyclerView.adapter = adapter

        builder.setView(dialogView)

        val closeButton = dialogView.findViewById<Button>(R.id.btn_close_dialog)
        closeButton.setOnClickListener {
            dismiss() // Dismiss the dialog when the close button is clicked
        }

        return builder.create()
    }
}
