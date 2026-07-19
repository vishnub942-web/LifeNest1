package com.vishnu.lifenest.ui.dailytask

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

/**
 * IMPORTANT: We only push edits (name/time/remarks) to the database when the
 * user leaves that field (focus lost), NOT on every keystroke. Saving on every
 * keystroke was causing the row to refresh mid-typing, which reset the cursor
 * to the start and scrambled the text (e.g. "happy" -> "yppah").
 */
class DailyTaskAdapter(
    private val onStatusTap: (TaskWithEntry) -> Unit,
    private val onTimeChanged: (TaskWithEntry, String?, String?) -> Unit,
    private val onRemarksChanged: (TaskWithEntry, String) -> Unit,
    private val onNameChanged: (TaskWithEntry, String) -> Unit,
    private val onLongPress: (TaskWithEntry) -> Unit = {}
) : ListAdapter<TaskWithEntry, DailyTaskAdapter.RowHolder>(DIFF) {

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

            // --- Task name ---
            editName.setText(item.taskName)
            editName.onFocusChangeListener = null
            editName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) onNameChanged(item, editName.text.toString())
            }

            // --- Status ---
            textStatus.text = when (item.status) {
                TaskStatus.DONE -> "✅"
                TaskStatus.MISSED -> "❌"
                else -> "▢"
            }
            textStatus.setOnClickListener { onStatusTap(item) }

            // --- Start / End time digits ---
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

            editStart.onFocusChangeListener = null
            editStart.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) pushTimeUpdate(item) }
            editEnd.onFocusChangeListener = null
            editEnd.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) pushTimeUpdate(item) }

            textSpent.text = TimeUtils.minutesToLabel(item.spentMinutes)

            // --- Remarks ---
            editRemarks.setText(item.remarks ?: "")
            editRemarks.onFocusChangeListener = null
            editRemarks.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) onRemarksChanged(item, editRemarks.text.toString())
            }
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
