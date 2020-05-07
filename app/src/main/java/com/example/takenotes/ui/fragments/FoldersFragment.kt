package com.example.takenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.R
import com.example.takenotes.data.model.api.Folder
import com.example.takenotes.ui.SharedViewModel
import com.example.takenotes.ui.adapters.FoldersAdapter
import kotlinx.android.synthetic.main.fragment_folders.*

class FoldersFragment : Fragment() {

    private val viewModel by lazy {
        return@lazy ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private val listener: OnFolderChooseClickListener = object :
        OnFolderChooseClickListener {
        override fun onItemClick(newlySelected: Folder) {
            viewModel.onFolderClicked(newlySelected)
        }
    }

    private val listAdapter = FoldersAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initLiveData()

        viewModel.setToolBarText()
    }

    private fun initLiveData() {
        viewModel.allFolders.observe(viewLifecycleOwner, Observer {
            listAdapter.setItems(it)
        })
    }

    private fun initViews() {
        rv_folders.adapter = listAdapter

        val dividerItemDecoration = DividerItemDecoration(context, RecyclerView.VERTICAL)
        rv_folders.addItemDecoration(dividerItemDecoration)

        iv_new_folder_folders_fragment.setOnClickListener { viewModel.onNewFolderClicked() }

    }

    interface OnFolderChooseClickListener {
        fun onItemClick(newlySelected: Folder)
    }
}
