package com.vishnu.lifenest.ui.daycounter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.EventEntity
import com.vishnu.lifenest.data.EventType
import com.vishnu.lifenest.util.DateMath

class EventAdapter(private val onLongPress: (EventEntity) -> Unit) :
    ListAdapter<EventEntity, EventAdapter.Holder>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(a: EventEntity, b: EventEntity) = a.id == b.id
            override fun areContentsTheSame(a: EventEntity, b: EventEntity) = a == b
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.text_event_title)
        private val detail: TextView = itemView.findViewById(R.id.text_event_detail)

        fun bind(event: EventEntity) {
            title.text = event.title
            itemView.setOnLongClickListener { onLongPress(event); true }

            val today = DateMath.today()
            val fromCal = DateMath.parseIso(event.fromDate)

            detail.text = if (event.type == EventType.YEARLY) {
                val next = DateMath.nextYearlyOccurrence(fromCal)
                val daysLeft = DateMath.daysBetween(today, next)
                if (daysLeft == 0) "🎉 Today!" else "Next occurrence in $daysLeft day${if (daysLeft != 1) "s" else ""}"
            } else {
                val toDateStr = event.toDate
                if (toDateStr.isNullOrBlank()) {
                    if (!fromCal.after(today)) {
                        "${DateMath.humanBreakdown(fromCal, today)} since ${event.fromDate}"
                    } else {
                        "Starts on ${event.fromDate}"
                    }
                } else {
                    val toCal = DateMath.parseIso(toDateStr)
                    val elapsed = if (!fromCal.after(today)) DateMath.humanBreakdown(fromCal, today) else "not started"
                    val remaining = if (toCal.after(today))
                        "${DateMath.daysBetween(today, toCal)} days left"
                    else "ended"
                    "Elapsed: $elapsed  •  Remaining: $remaining"
                }
            }
        }
    }
}
