package com.vishnu.lifenest.ui.creditdebt

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.LedgerEntity
import com.vishnu.lifenest.data.LedgerType

class LedgerAdapter(
    private val onSettleTap: (LedgerEntity) -> Unit,
    private val onLongPress: (LedgerEntity) -> Unit
) : ListAdapter<LedgerEntity, LedgerAdapter.Holder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<LedgerEntity>() {
            override fun areItemsTheSame(a: LedgerEntity, b: LedgerEntity) = a.id == b.id
            override fun areContentsTheSame(a: LedgerEntity, b: LedgerEntity) = a == b
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ledger, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.text_ledger_name)
        private val note: TextView = itemView.findViewById(R.id.text_ledger_note)
        private val date: TextView = itemView.findViewById(R.id.text_ledger_date)
        private val amount: TextView = itemView.findViewById(R.id.text_ledger_amount)
        private val status: TextView = itemView.findViewById(R.id.text_ledger_status)

        fun bind(entry: LedgerEntity) {
            name.text = entry.personName
            note.text = entry.note
            note.visibility = if (entry.note.isBlank()) android.view.View.GONE else android.view.View.VISIBLE
            date.text = entry.dateStr

            val colorRes = if (entry.type == LedgerType.CREDIT) R.color.green_success else R.color.red_error
            val sign = if (entry.type == LedgerType.CREDIT) "+" else "-"
            amount.text = "$sign${"%.2f".format(entry.amount)}"
            amount.setTextColor(ContextCompat.getColor(itemView.context, colorRes))

            status.text = if (entry.settled) "✅" else "▢"
            status.setOnClickListener { onSettleTap(entry) }
            itemView.setOnLongClickListener { onLongPress(entry); true }

            itemView.alpha = if (entry.settled) 0.55f else 1.0f
        }
    }
}
