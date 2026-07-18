package com.vishnu.lifenest.ui.dailytask

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.TaskWithEntry
import java.text.SimpleDateFormat
import java.util.*

class DailyTaskFragment : Fragment() {

    private lateinit var viewModel: DailyTaskViewModel
    private lateinit var adapter: DailyTaskAdapter
    private val displayFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_daily_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DailyTaskViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_daily_tasks)
        val dateHeader = view.findViewById<TextView>(R.id.text_date_header)
        val addButton = view.findViewById<View>(R.id.btn_add_task)

        adapter = DailyTaskAdapter(
            onStatusTap = { item -> viewModel.cycleStatus(item) },
            onTimeChanged = { item, start, end -> viewModel.updateTime(item, start, end) },
            onRemarksChanged = { item, text -> viewModel.updateRemarks(item, text) },
            onNameChanged = { item, text -> viewModel.renameTask(item.taskId, text) },
            onLongPress = { item -> showDeleteConfirm(item) }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.selectedDate.observe(viewLifecycleOwner) { dateStr ->
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)
            dateHeader.text = if (parsed != null) "Daily Tasks - ${displayFormat.format(parsed)}" else "Daily Tasks"
        }

        viewModel.tasksForDate.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        addButton.setOnClickListener { showAddTaskDialog() }
    }

    private fun showAddTaskDialog() {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Task name"

        AlertDialog.Builder(requireContext())
            .setTitle("Add Task")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                viewModel.addTask(input.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirm(item: TaskWithEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove task?")
            .setMessage("\"${item.taskName}\" will be removed from all days.")
            .setPositiveButton("Remove") { _, _ -> viewModel.deleteTask(item.taskId) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
