package com.example.takenotes.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.takenotes.R
import com.example.takenotes.data.model.api.Note
import com.example.takenotes.ui.fragments.NoteListFragment
import kotlinx.android.synthetic.main.note_element.view.*
import java.lang.ref.WeakReference

class NoteListAdapter(
    val listener: NoteListFragment.OnNoteChooseClickListener
) : RecyclerView.Adapter<NoteListAdapter.NoteViewHolder>() {

    private val allNotes = mutableListOf<Note>()
    private var selected = mutableListOf<Note>()

    fun setItems(newItems: List<Note>) {
        allNotes.clear()
        allNotes.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setSelected(newItems: List<Note>) {
        selected.clear()
        selected.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.note_element, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = allNotes[position]
        holder.bind(item)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
      //  val item = allNotes[viewHolder.adapterPosition].id
        allNotes.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return allNotes.size
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var item: WeakReference<Note>
        private val textViewTitle = itemView.tv_note_element_title
        private val textViewBody = itemView.tv_note_element
        private val ivSelected = itemView.iv_selected

        fun bind(item: Note) {
            this.item = WeakReference(item)

            ivSelected.isVisible = selected.contains(item)

            itemView.setOnClickListener { listener.onItemClick(item) }
            itemView.setOnLongClickListener {
                if (selected.contains(item)) {
                    selected.remove(item)
                    ivSelected.isVisible = false
                } else {
                    selected.add(item)
                    ivSelected.isVisible = true
                }
                listener.onItemLongClick(item)
                true
            }

            textViewTitle.text = item.title
            textViewBody.text = item.noteText.substringAfter(item.title).trimStart()
        }
    }
}