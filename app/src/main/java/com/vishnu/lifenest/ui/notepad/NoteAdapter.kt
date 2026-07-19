package com.vishnu.lifenest.ui.notepad

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.NoteEntity

class NoteAdapter(private val onClick: (NoteEntity) -> Unit) :
    ListAdapter<NoteEntity, NoteAdapter.Holder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<NoteEntity>() {
            override fun areItemsTheSame(a: NoteEntity, b: NoteEntity) = a.id == b.id
            override fun areContentsTheSame(a: NoteEntity, b: NoteEntity) = a == b
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val heading: TextView = itemView.findViewById(R.id.text_note_heading)
        private val modified: TextView = itemView.findViewById(R.id.text_note_modified)

        fun bind(note: NoteEntity) {
            heading.text = note.heading.ifBlank { "(untitled)" }
            modified.text = "Modified: ${note.modifiedAt}"
            itemView.setOnClickListener { onClick(note) }
        }
    }
}
