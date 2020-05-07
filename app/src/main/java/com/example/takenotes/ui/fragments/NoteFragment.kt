package com.example.takenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.takenotes.R
import com.example.takenotes.ui.SharedViewModel
import kotlinx.android.synthetic.main.fragment_note.*
import java.text.SimpleDateFormat
import java.util.*

class NoteFragment : Fragment() {

    private val viewModel by lazy {
        return@lazy ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initLiveData()
    }

    private fun initViews() {
        iv_new_note_note_fragment.setOnClickListener { viewModel.onNewNoteClicked() }

        iv_delete_note.setOnClickListener { viewModel.deleteCurrentNote() }

        iv_save_note.setOnClickListener { viewModel.saveChanges() }

        et_note_body.addTextChangedListener(
            afterTextChanged = {
                viewModel.onCurrentNoteBodyChanged(it?.toString() ?: "")
            }
        )
    }

    private fun initLiveData() {
        viewModel.currentNote.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            et_note_body.setText(it.noteText)

            val pattern = "MMMM d yyyy, KK:mm a"
            val formatter = SimpleDateFormat(pattern)
            tv_date_and_time.text = formatter.format(Date(it.dateModified.toLong()))
        })
    }
}
