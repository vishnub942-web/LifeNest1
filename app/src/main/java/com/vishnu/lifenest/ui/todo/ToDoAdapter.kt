package com.vishnu.lifenest.ui.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.ToDoEntity
import com.vishnu.lifenest.data.ToDoStatus

class ToDoAdapter(
    private val onStatusTap: (ToDoEntity) -> Unit,
    private val onLongPress: (ToDoEntity) -> Unit
) : ListAdapter<ToDoEntity, ToDoAdapter.Holder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ToDoEntity>() {
            override fun areItemsTheSame(a: ToDoEntity, b: ToDoEntity) = a.id == b.id
            override fun areContentsTheSame(a: ToDoEntity, b: ToDoEntity) = a == b
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.text_todo_text)
        private val added: TextView = itemView.findViewById(R.id.text_added_at)
        private val marked: TextView = itemView.findViewById(R.id.text_marked_at)
        private val status: TextView = itemView.findViewById(R.id.text_todo_status)

        fun bind(item: ToDoEntity) {
            text.text = item.text
            added.text = "Added: ${item.addedAt}"
            marked.text = if (item.markedAt != null) "Marked: ${item.markedAt}" else ""
            status.text = when (item.status) {
                ToDoStatus.DONE -> "✅"
                ToDoStatus.NOT_DONE -> "❌"
                else -> "▢"
            }
            status.setOnClickListener { onStatusTap(item) }
            itemView.setOnLongClickListener { onLongPress(item); true }
        }
    }
}
