package com.vishnu.lifenest.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.TaskCompletion
import com.vishnu.lifenest.data.TaskStatus
import com.vishnu.lifenest.data.TaskWithEntry
import java.text.SimpleDateFormat
import java.util.*

class TaskCalendarFragment : Fragment() {

    private lateinit var viewModel: TaskCalendarViewModel
    private val dateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US)
    private val monthLabelFmt = SimpleDateFormat("MMMM yyyy", Locale.US)

    private lateinit var gridCalendar: GridLayout
    private lateinit var monthLabel: TextView
    private lateinit var selectedDateLabel: TextView
    private lateinit var selectedTasksContainer: LinearLayout
    private lateinit var completionContainer: LinearLayout

    private var currentHighlighted: Set<String> = emptySet()
    private var currentSelected: String = ""
    private var highlightedTaskId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[TaskCalendarViewModel::class.java]

        gridCalendar = view.findViewById(R.id.grid_calendar)
        monthLabel = view.findViewById(R.id.text_month_label)
        selectedDateLabel = view.findViewById(R.id.text_selected_date_label)
        selectedTasksContainer = view.findViewById(R.id.container_selected_date_tasks)
        completionContainer = view.findViewById(R.id.container_completion_report)

        view.findViewById<View>(R.id.btn_prev_month).setOnClickListener { viewModel.changeMonth(-1) }
        view.findViewById<View>(R.id.btn_next_month).setOnClickListener { viewModel.changeMonth(1) }

        viewModel.visibleMonth.observe(viewLifecycleOwner) { ym ->
            val d = monthFmt.parse(ym) ?: Date()
            monthLabel.text = monthLabelFmt.format(d)
            drawGrid(ym)
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            currentSelected = date
            selectedDateLabel.text = "Tasks on $date"
            drawGrid(viewModel.visibleMonth.value ?: monthFmt.format(Date()))
        }

        viewModel.tasksForSelectedDate.observe(viewLifecycleOwner) { list ->
            renderSelectedDateTasks(list)
        }

        viewModel.monthlyCompletion.observe(viewLifecycleOwner) { list ->
            renderCompletionReport(list)
        }

        viewModel.highlightedDates.observe(viewLifecycleOwner) { set ->
            currentHighlighted = set
            drawGrid(viewModel.visibleMonth.value ?: monthFmt.format(Date()))
        }
    }

    private fun drawGrid(yearMonth: String) {
        gridCalendar.removeAllViews()
        gridCalendar.columnCount = 7

        // weekday headers
        val headers = listOf("S", "M", "T", "W", "T", "F", "S")
        for (h in headers) {
            gridCalendar.addView(makeCell(h, isHeader = true))
        }

        val cal = Calendar.getInstance()
        cal.time = monthFmt.parse(yearMonth) ?: Date()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1 until firstDayOfWeek) {
            gridCalendar.addView(makeCell("", isHeader = false))
        }

        val todayStr = dateFmt.format(Date())

        for (day in 1..maxDay) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateStr = dateFmt.format(cal.time)
            val cell = makeCell(day.toString(), isHeader = false)
            when {
                dateStr == currentSelected -> {
                    cell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_accent))
                    cell.setTextColor(Color.BLACK)
                }
                currentHighlighted.contains(dateStr) -> {
                    cell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_success))
                    cell.setTextColor(Color.WHITE)
                }
                dateStr == todayStr -> {
                    cell.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_dark))
                }
            }
            cell.setOnClickListener { viewModel.selectDate(dateStr) }
            gridCalendar.addView(cell)
        }
    }

    private fun makeCell(text: String, isHeader: Boolean): TextView {
        val tv = TextView(requireContext())
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.textSize = if (isHeader) 12f else 14f
        tv.setTextColor(ContextCompat.getColor(requireContext(), if (isHeader) R.color.text_secondary else R.color.text_primary))
        val params = GridLayout.LayoutParams()
        params.width = 0
        params.height = (40 * resources.displayMetrics.density).toInt()
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        params.setMargins(2, 8, 2, 8)
        tv.layoutParams = params
        return tv
    }

    private fun renderSelectedDateTasks(list: List<TaskWithEntry>) {
        selectedTasksContainer.removeAllViews()
        if (list.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "No tasks yet."
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            selectedTasksContainer.addView(tv)
            return
        }
        for (item in list) {
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(0, 8, 0, 8)

            val name = TextView(requireContext())
            name.text = item.taskName
            name.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            name.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

            val status = TextView(requireContext())
            status.text = when (item.status) {
                TaskStatus.DONE -> "✅"
                TaskStatus.MISSED -> "❌"
                else -> "▢"
            }

            row.addView(name)
            row.addView(status)
            selectedTasksContainer.addView(row)
        }
    }

    private fun renderCompletionReport(list: List<TaskCompletion>) {
        completionContainer.removeAllViews()
        for (item in list) {
            val row = LinearLayout(requireContext())
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(12, 10, 12, 10)
            val isSelected = item.taskId == highlightedTaskId
            if (isSelected) {
                row.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_dark))
            }

            val name = TextView(requireContext())
            name.text = item.taskName
            name.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.teal_accent else R.color.purple_primary
                )
            )
            if (isSelected) name.setTypeface(name.typeface, android.graphics.Typeface.BOLD)
            name.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            name.setOnClickListener {
                highlightedTaskId = item.taskId
                viewModel.highlightTaskDates(item.taskId)
                renderCompletionReport(list)
            }

            val count = TextView(requireContext())
            val total = if (item.totalDaysSoFar > 0) item.totalDaysSoFar else 1
            count.text = "${item.doneCount}/$total days"
            count.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))

            row.addView(name)
            row.addView(count)
            completionContainer.addView(row)
        }
    }
}
