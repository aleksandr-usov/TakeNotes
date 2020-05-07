package com.example.takenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.takenotes.R
import com.example.takenotes.ui.SharedViewModel
import com.example.takenotes.ui.adapters.SortAdapter
import kotlinx.android.synthetic.main.fragment_sort_choose_list.*

class SortChooseFragment : Fragment() {

    private val viewModel by lazy {
        return@lazy ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    private val listener: OnSortChooseClickListener = object :
        OnSortChooseClickListener {
        override fun onItemClick(newlySelected: SharedViewModel.SortToChoose) {
            viewModel.onSortItemClicked(newlySelected)
        }
    }

    private val listAdapter = SortAdapter(listener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort_choose_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setToolBarText()

        viewModel.sortLiveData.observe(viewLifecycleOwner, Observer {
            listAdapter.setItems(it ?: listOf())
        })
        rv_sort_choose_list.adapter = listAdapter
    }

    interface OnSortChooseClickListener {
        fun onItemClick(newlySelected: SharedViewModel.SortToChoose)
    }
}
