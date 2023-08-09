package com.knowledge.testapp.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowledge.testapp.QuizValues
import com.knowledge.testapp.R
import com.knowledge.testapp.adapters.PathDataAdapter
import com.knowledge.testapp.data.PathRecord

class PathsDialogFragment(private val paths: ArrayList<PathRecord>, private val isWinning: Boolean) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_paths_dialog, null)

        val linearLayout = dialogView.findViewById<LinearLayout>(R.id.ll_user_local_paths_dialog)

        val titleTextViewUser = dialogView.findViewById<TextView>(R.id.tv_local_paths_username)
        titleTextViewUser.text = QuizValues.USER!!.username

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_local_paths_result)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvUserLocalPaths)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = PathDataAdapter(paths)
        recyclerView.adapter = adapter


        recyclerView.adapter = adapter

        if (isWinning) {
            linearLayout.setBackgroundResource(R.drawable.foundcorrectpathwin3)
            tvTitle.text = "Winning Paths"
        } else {
            linearLayout.setBackgroundResource(R.drawable.deadendpathlose)
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
