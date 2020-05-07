package com.example.takenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.takenotes.R
import com.example.takenotes.ui.SharedViewModel
import kotlinx.android.synthetic.main.fragment_dialog_folder_name.*

class NewFolderNameDialogFragment : DialogFragment() {

   private val viewModel by lazy {
      return@lazy ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
   }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_folder_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_ok_folder.setOnClickListener {
            val enteredName = et_folder_name.text.toString()
            if (enteredName.isNotBlank()) {
                viewModel.insertNewFolder(enteredName)
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Empty name!", LENGTH_SHORT).show()
            }
        }
        btn_cancel_folder.setOnClickListener { dismiss() }
    }

}