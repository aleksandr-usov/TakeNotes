package com.example.takenotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.R
import com.example.takenotes.data.model.api.Folder
import com.example.takenotes.ui.fragments.FoldersFragment
import kotlinx.android.synthetic.main.folders_element.view.*
import java.lang.ref.WeakReference

class FoldersAdapter(
    val listener: FoldersFragment.OnFolderChooseClickListener
) : RecyclerView.Adapter<FoldersAdapter.FolderViewHolder>() {

    private val allFolders = mutableListOf<Folder>()

    fun setItems(newItems: List<Folder>) {
        allFolders.clear()
        allFolders.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FolderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.folders_element, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val item = allFolders[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return allFolders.size
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var item: WeakReference<Folder>
        private val textViewTitle = itemView.tv_folders_text

        fun bind(item: Folder) {
            this.item = WeakReference(item)

            itemView.setOnClickListener { listener.onItemClick(item) }

            textViewTitle.text = item.title
        }
    }
}