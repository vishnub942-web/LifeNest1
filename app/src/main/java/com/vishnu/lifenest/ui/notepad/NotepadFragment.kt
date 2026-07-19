package com.vishnu.lifenest.ui.notepad

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishnu.lifenest.R

class NotepadFragment : Fragment() {

    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_notepad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_notes)
        adapter = NoteAdapter { note -> openDetail(note.id) }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        viewModel.notes.observe(viewLifecycleOwner) { adapter.submitList(it) }

        view.findViewById<View>(R.id.btn_add_note).setOnClickListener { openDetail(null) }

        val searchBox = view.findViewById<EditText>(R.id.edit_search_notes)
        searchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
        })
    }

    private fun openDetail(noteId: Long?) {
        val fragment = NoteDetailFragment.newInstance(noteId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
