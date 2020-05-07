package com.example.takenotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.R
import com.example.takenotes.ui.SharedViewModel
import com.example.takenotes.ui.fragments.SortChooseFragment
import kotlinx.android.synthetic.main.sort_choose_element.view.*

class SortAdapter constructor(
    val listener: SortChooseFragment.OnSortChooseClickListener
) : RecyclerView.Adapter<SortAdapter.SortViewHolder>() {

    private val sortChooseList = mutableListOf<SharedViewModel.SortToChoose>()

    fun setItems(newItems: List<SharedViewModel.SortToChoose>) {
        sortChooseList.clear()
        sortChooseList.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sort_choose_element, parent, false)
        return SortViewHolder(view)
    }

    override fun onBindViewHolder(holder: SortViewHolder, position: Int) {
        val item = sortChooseList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return sortChooseList.size
    }

    inner class SortViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.tv_sort_choose_element

        fun bind(item: SharedViewModel.SortToChoose) {
            itemView.setOnClickListener { listener.onItemClick(item) }
            textView.text = item.displayableName
        }
    }

}

