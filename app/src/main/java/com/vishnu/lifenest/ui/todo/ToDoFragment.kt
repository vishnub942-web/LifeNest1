package com.vishnu.lifenest.ui.todo

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.ToDoEntity

class ToDoFragment : Fragment() {

    private lateinit var viewModel: ToDoViewModel
    private lateinit var adapter: ToDoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ToDoViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_todo)
        adapter = ToDoAdapter(
            onStatusTap = { item -> viewModel.cycleStatus(item) },
            onLongPress = { item -> showDeleteConfirm(item) }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.items.observe(viewLifecycleOwner) { adapter.submitList(it) }

        view.findViewById<View>(R.id.btn_add_todo).setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "What needs to be done?"
        AlertDialog.Builder(requireContext())
            .setTitle("Add To Do")
            .setView(input)
            .setPositiveButton("Add") { _, _ -> viewModel.addItem(input.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirm(item: ToDoEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete this item?")
            .setMessage(item.text)
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(item) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
