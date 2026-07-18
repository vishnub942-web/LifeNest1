package com.vishnu.lifenest.ui.dailytask

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.TaskStatus
import com.vishnu.lifenest.data.TaskWithEntry
import com.vishnu.lifenest.util.TimeUtils

class DailyTaskAdapter(
    private val onStatusTap: (TaskWithEntry) -> Unit,
    private val onTimeChanged: (TaskWithEntry, String?, String?) -> Unit,
    private val onRemarksChanged: (TaskWithEntry, String) -> Unit,
    private val onNameChanged: (TaskWithEntry, String) -> Unit,
    private val onLongPress: (TaskWithEntry) -> Unit = {}
) : ListAdapter<TaskWithEntry, DailyTaskAdapter.RowHolder>(DIFF) {

    // tracks AM/PM per row locally, keyed by taskId
    private val startIsAm = HashMap<Long, Boolean>()
    private val endIsAm = HashMap<Long, Boolean>()

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<TaskWithEntry>() {
            override fun areItemsTheSame(a: TaskWithEntry, b: TaskWithEntry) = a.taskId == b.taskId
            override fun areContentsTheSame(a: TaskWithEntry, b: TaskWithEntry) = a == b
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_task, parent, false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** Sets a text watcher that replaces any previous one set via this helper (avoids duplicate callbacks on recycled rows). */
    private fun EditText.setChangeListener(action: (String) -> Unit) {
        (tag as? TextWatcher)?.let { removeTextChangedListener(it) }
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = action(s?.toString() ?: "")
        }
        addTextChangedListener(watcher)
        tag = watcher
    }

    inner class RowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val editName: EditText = itemView.findViewById(R.id.edit_task_name)
        private val textStatus: TextView = itemView.findViewById(R.id.text_status)
        private val editStart: EditText = itemView.findViewById(R.id.edit_start_time)
        private val editEnd: EditText = itemView.findViewById(R.id.edit_end_time)
        private val textStartMeridiem: TextView = itemView.findViewById(R.id.text_start_meridiem)
        private val textEndMeridiem: TextView = itemView.findViewById(R.id.text_end_meridiem)
        private val textSpent: TextView = itemView.findViewById(R.id.text_spent_time)
        private val editRemarks: EditText = itemView.findViewById(R.id.edit_remarks)

        fun bind(item: TaskWithEntry) {
            itemView.setOnLongClickListener { onLongPress(item); true }
            editName.setText(item.taskName)
            editName.setChangeListener { text -> onNameChanged(item, text) }

            textStatus.text = when (item.status) {
                TaskStatus.DONE -> "✅"
                TaskStatus.MISSED -> "❌"
                else -> "▢"
            }
            textStatus.setOnClickListener { onStatusTap(item) }

            val startDigits = item.startTime?.substringBefore(" ")?.replace(":", "") ?: ""
            val endDigits = item.endTime?.substringBefore(" ")?.replace(":", "") ?: ""
            editStart.setText(startDigits)
            editEnd.setText(endDigits)

            startIsAm[item.taskId] = item.startTime?.endsWith("AM") ?: true
            endIsAm[item.taskId] = item.endTime?.endsWith("AM") ?: true
            textStartMeridiem.text = if (startIsAm[item.taskId] == true) "AM" else "PM"
            textEndMeridiem.text = if (endIsAm[item.taskId] == true) "AM" else "PM"

            textStartMeridiem.setOnClickListener {
                startIsAm[item.taskId] = startIsAm[item.taskId] != true
                textStartMeridiem.text = if (startIsAm[item.taskId] == true) "AM" else "PM"
                pushTimeUpdate(item)
            }
            textEndMeridiem.setOnClickListener {
                endIsAm[item.taskId] = endIsAm[item.taskId] != true
                textEndMeridiem.text = if (endIsAm[item.taskId] == true) "AM" else "PM"
                pushTimeUpdate(item)
            }

            editStart.setChangeListener { pushTimeUpdate(item) }
            editEnd.setChangeListener { pushTimeUpdate(item) }

            textSpent.text = TimeUtils.minutesToLabel(item.spentMinutes)

            editRemarks.setText(item.remarks ?: "")
            editRemarks.setChangeListener { text -> onRemarksChanged(item, text) }
        }

        private fun pushTimeUpdate(item: TaskWithEntry) {
            val startRaw = editStart.text.toString()
            val endRaw = editEnd.text.toString()
            val startFormatted = if (startRaw.isNotBlank())
                TimeUtils.withMeridiem(TimeUtils.formatDigitsToTime(startRaw), startIsAm[item.taskId] ?: true)
            else null
            val endFormatted = if (endRaw.isNotBlank())
                TimeUtils.withMeridiem(TimeUtils.formatDigitsToTime(endRaw), endIsAm[item.taskId] ?: true)
            else null
            onTimeChanged(item, startFormatted, endFormatted)
        }
    }
}
