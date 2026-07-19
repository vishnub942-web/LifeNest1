package com.vishnu.lifenest.ui.daycounter

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.EventEntity
import java.text.SimpleDateFormat
import java.util.*

class DayCounterFragment : Fragment() {

    private lateinit var viewModel: DayCounterViewModel
    private lateinit var adapter: EventAdapter
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_daycounter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DayCounterViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_events)
        adapter = EventAdapter { event -> showDeleteConfirm(event) }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.events.observe(viewLifecycleOwner) { adapter.submitList(it) }

        view.findViewById<View>(R.id.btn_add_event).setOnClickListener { showTypeChoiceDialog() }
    }

    private fun showTypeChoiceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("New Event")
            .setItems(arrayOf("Yearly (birthday / anniversary)", "From date -> To date")) { _, which ->
                if (which == 0) showYearlyDialog() else showRangeDialog()
            }
            .show()
    }

    private fun showYearlyDialog() {
        val layout = simpleFormLayout()
        val titleInput = EditText(requireContext()).apply { hint = "Title (e.g. Amma's birthday)" }
        val dateButton = android.widget.Button(requireContext()).apply { text = "Pick date" }
        var pickedDate: String? = null
        dateButton.setOnClickListener {
            pickDate { date ->
                pickedDate = date
                dateButton.text = date
            }
        }
        layout.addView(titleInput)
        layout.addView(dateButton)

        AlertDialog.Builder(requireContext())
            .setTitle("Yearly Event")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                pickedDate?.let { viewModel.addYearlyEvent(titleInput.text.toString(), it) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRangeDialog() {
        val layout = simpleFormLayout()
        val titleInput = EditText(requireContext()).apply { hint = "Title (e.g. Project deadline)" }
        val fromButton = android.widget.Button(requireContext()).apply { text = "Pick From date" }
        val toButton = android.widget.Button(requireContext()).apply { text = "Pick To date (optional)" }
        var fromDate: String? = null
        var toDate: String? = null
        fromButton.setOnClickListener { pickDate { d -> fromDate = d; fromButton.text = d } }
        toButton.setOnClickListener { pickDate { d -> toDate = d; toButton.text = d } }
        layout.addView(titleInput)
        layout.addView(fromButton)
        layout.addView(toButton)

        AlertDialog.Builder(requireContext())
            .setTitle("From / To Event")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                fromDate?.let { viewModel.addRangeEvent(titleInput.text.toString(), it, toDate) }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun simpleFormLayout(): LinearLayout {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(48, 24, 48, 24)
        return layout
    }

    /** Opens a DatePickerDialog that also lets the user go to any past year (e.g. birth year). */
    private fun pickDate(onPicked: (String) -> Unit) {
        val cal = Calendar.getInstance()
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val picked = Calendar.getInstance()
                picked.set(year, month, day)
                onPicked(isoFormat.format(picked.time))
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun showDeleteConfirm(event: EventEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete this event?")
            .setMessage(event.title)
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(event) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
