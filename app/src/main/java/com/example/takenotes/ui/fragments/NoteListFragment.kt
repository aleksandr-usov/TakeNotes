package com.example.takenotes.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.R
import com.example.takenotes.data.model.api.Note
import com.example.takenotes.ui.SharedViewModel
import com.example.takenotes.ui.adapters.NoteListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_note_list.*

class NoteListFragment : Fragment() {

    private val viewModel by lazy {
        return@lazy ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private val listener: OnNoteChooseClickListener = object :
        OnNoteChooseClickListener {
        override fun onItemClick(newlySelected: Note) {
            viewModel.onNoteClicked(newlySelected)
        }

        override fun onItemLongClick(newlySelected: Note) {
            viewModel.onNoteLongClicked(newlySelected)
        }
    }

    private val listAdapter = NoteListAdapter(listener)

    private val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (viewHolder !is NoteListAdapter.NoteViewHolder) return
                viewModel.deleteOnSwipe(viewHolder.item.get() ?: return)
                listAdapter.removeItem(viewHolder)
                Snackbar.make(viewHolder.itemView, "", Snackbar.LENGTH_LONG)
                    .setAction("A NOTE HAS BEEN DELETED! UNDO") {
                        viewModel.undoDeleteItem()
                    }.setActionTextColor(Color.WHITE).show()
            }
        }

    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    private val popup by lazy {
        PopupMenu(requireContext(), v_sort_notes, Gravity.END)
            .apply {
                inflate(R.menu.sort_menu)
                setOnMenuItemClickListener {
                    val sortBy = when (it.itemId) {
                        R.id.sort_by_created -> {
                            SharedViewModel.SortToChoose.CREATED
                        }

                        R.id.sort_by_last_modified -> {
                            SharedViewModel.SortToChoose.MODIFIED
                        }

                        else -> return@setOnMenuItemClickListener false
                    }

                    viewModel.onSortTypeClicked(sortBy)
                    tv_sort_notes_text.text = sortBy.displayableName
                    true
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initLiveData()
    }

    private fun initViews() {

        et_search_notes.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel.searchNotes(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        })

        itemTouchHelper.attachToRecyclerView(rv_notes_list)

        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        rv_notes_list.addItemDecoration(dividerItemDecoration)

        rv_notes_list.adapter = listAdapter

        v_sort_notes.setOnClickListener {
            popup.show()
        }

        iv_new_note_note_list_fragment.setOnClickListener { viewModel.onNewNoteClicked() }

        iv_delete_note_note_list_fragment.setOnClickListener {
            viewModel.deleteMultipleNotes()
            it.isVisible = false
        }

        viewModel.setToolBarText()
    }

    private fun initLiveData() {
        viewModel.notesToDisplay.observe(viewLifecycleOwner, Observer {

            val size = it.size

            if (it.size == 1) {
                tv_notes_count.text = "$size note"
            } else {
                tv_notes_count.text = "$size notes"
            }
        })

        viewModel.currentSortLiveData.observe(viewLifecycleOwner, Observer {
            tv_sort_notes_text.text = it.displayableName
        })

        viewModel.notesToDisplay.observe(viewLifecycleOwner, Observer {
            listAdapter.setItems(it)
        })

        viewModel.selectedNotes.observe(viewLifecycleOwner, Observer {
            iv_delete_note_note_list_fragment.isVisible = !it.isNullOrEmpty()
            listAdapter.setSelected(it)
        })
    }

    interface OnNoteChooseClickListener {
        fun onItemClick(newlySelected: Note)
        fun onItemLongClick(newlySelected: Note)
    }
}
