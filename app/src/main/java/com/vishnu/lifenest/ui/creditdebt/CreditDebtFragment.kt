package com.vishnu.lifenest.ui.creditdebt

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.LedgerEntity
import com.vishnu.lifenest.data.LedgerType
import java.text.SimpleDateFormat
import java.util.*

class CreditDebtFragment : Fragment() {

    private lateinit var viewModel: CreditDebtViewModel
    private lateinit var adapter: LedgerAdapter
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_creditdebt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreditDebtViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_ledger)
        adapter = LedgerAdapter(
            onSettleTap = { entry -> viewModel.toggleSettled(entry) },
            onLongPress = { entry -> showDeleteConfirm(entry) }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.visibleEntries.observe(viewLifecycleOwner) { adapter.submitList(it) }

        val textCredit = view.findViewById<android.widget.TextView>(R.id.text_total_credit)
        val textDebit = view.findViewById<android.widget.TextView>(R.id.text_total_debit)
        val textNet = view.findViewById<android.widget.TextView>(R.id.text_total_net)
        viewModel.totals.observe(viewLifecycleOwner) { totals ->
            textCredit.text = "+${"%.2f".format(totals.credit)}"
            textDebit.text = "-${"%.2f".format(totals.debit)}"
            textNet.text = "%.2f".format(totals.net)
        }

        val btnAll = view.findViewById<Button>(R.id.btn_filter_all)
        val btnCredit = view.findViewById<Button>(R.id.btn_filter_credit)
        val btnDebit = view.findViewById<Button>(R.id.btn_filter_debit)
        btnAll.setOnClickListener { viewModel.setFilter(LedgerFilter.ALL); highlightFilter(btnAll, btnCredit, btnDebit, btnAll) }
        btnCredit.setOnClickListener { viewModel.setFilter(LedgerFilter.CREDIT); highlightFilter(btnAll, btnCredit, btnDebit, btnCredit) }
        btnDebit.setOnClickListener { viewModel.setFilter(LedgerFilter.DEBIT); highlightFilter(btnAll, btnCredit, btnDebit, btnDebit) }

        view.findViewById<View>(R.id.btn_add_ledger).setOnClickListener { showAddDialog() }
    }

    private fun highlightFilter(all: Button, credit: Button, debit: Button, selected: Button) {
        for (b in listOf(all, credit, debit)) {
            b.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(), if (b == selected) R.color.purple_primary else R.color.surface_dark
            )
        }
    }

    private fun showAddDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(48, 24, 48, 24)

        val nameInput = EditText(requireContext()).apply { hint = "Person's name" }
        val amountInput = EditText(requireContext()).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val noteInput = EditText(requireContext()).apply { hint = "Note (what's this for?)" }

        var type = LedgerType.CREDIT
        val typeButton = Button(requireContext()).apply {
            text = "Type: Credit (they owe me)"
            backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_success)
        }
        typeButton.setOnClickListener {
            type = if (type == LedgerType.CREDIT) LedgerType.DEBIT else LedgerType.CREDIT
            if (type == LedgerType.CREDIT) {
                typeButton.text = "Type: Credit (they owe me)"
                typeButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_success)
            } else {
                typeButton.text = "Type: Debit (I owe them)"
                typeButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red_error)
            }
        }

        var dateStr = isoFormat.format(Date())
        val dateButton = Button(requireContext()).apply { text = "Date: $dateStr" }
        dateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                val picked = Calendar.getInstance()
                picked.set(y, m, d)
                dateStr = isoFormat.format(picked.time)
                dateButton.text = "Date: $dateStr"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        layout.addView(nameInput)
        layout.addView(amountInput)
        layout.addView(noteInput)
        layout.addView(typeButton)
        layout.addView(dateButton)

        AlertDialog.Builder(requireContext())
            .setTitle("New Entry")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.addEntry(nameInput.text.toString(), amount, type, noteInput.text.toString(), dateStr)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirm(entry: LedgerEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete this entry?")
            .setMessage(entry.personName)
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(entry) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
