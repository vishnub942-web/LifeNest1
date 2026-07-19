package com.vishnu.lifenest.ui.notepad

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.vishnu.lifenest.R
import com.vishnu.lifenest.data.NoteEntity
import kotlinx.coroutines.launch

/** Shown both for creating a brand-new note (noteId = null) and editing an existing one. */
class NoteDetailFragment : Fragment() {

    private lateinit var viewModel: NoteViewModel
    private var existingNote: NoteEntity? = null

    companion object {
        private const val ARG_NOTE_ID = "note_id"
        fun newInstance(noteId: Long?): NoteDetailFragment {
            val fragment = NoteDetailFragment()
            val args = Bundle()
            if (noteId != null) args.putLong(ARG_NOTE_ID, noteId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val editHeading = view.findViewById<EditText>(R.id.edit_note_heading)
        val editContent = view.findViewById<EditText>(R.id.edit_note_content)
        val textDates = view.findViewById<TextView>(R.id.text_note_dates)
        val btnBack = view.findViewById<View>(R.id.btn_note_back)
        val btnDelete = view.findViewById<View>(R.id.btn_note_delete)

        val noteId = if (arguments?.containsKey(ARG_NOTE_ID) == true) arguments!!.getLong(ARG_NOTE_ID) else null

        if (noteId != null) {
            lifecycleScope.launch {
                val note = viewModel.getById(noteId)
                existingNote = note
                if (note != null) {
                    editHeading.setText(note.heading)
                    editContent.setText(note.content)
                    textDates.text = "Created: ${note.createdAt}\nLast modified: ${note.modifiedAt}"
                }
            }
        } else {
            btnDelete.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            saveAndExit(editHeading, editContent)
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete this note?")
                .setPositiveButton("Delete") { _, _ ->
                    existingNote?.let { viewModel.deleteNote(it) }
                    parentFragmentManager.popBackStack()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun saveAndExit(editHeading: EditText, editContent: EditText) {
        val heading = editHeading.text.toString()
        val content = editContent.text.toString()
        if (heading.isNotBlank() || content.isNotBlank()) {
            val current = existingNote
            if (current == null) {
                viewModel.addNote(heading, content) {}
            } else {
                viewModel.updateNote(current, heading, content)
            }
        }
        parentFragmentManager.popBackStack()
    }
}
