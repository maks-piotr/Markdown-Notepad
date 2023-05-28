package com.markdown_notepad.edit_activity_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.markdown_notepad.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.markdown_notepad.AddTagsActivity
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditNoteDetailsFragment : BottomSheetDialogFragment() {
    private lateinit var viewModel: EditActivityViewModel
    private lateinit var noteTitleEditText: TextInputEditText
    private lateinit var acceptButton : MaterialButton
    private lateinit var closeButton: MaterialButton
    private lateinit var editTagsButton : MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_edit_note_details, container, false)
        viewModel = activity?.run {
            ViewModelProvider(this)[EditActivityViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        noteTitleEditText = fragment.findViewById(R.id.fragmentNoteTitleInputText)
        viewModel.noteTitle.observe(viewLifecycleOwner) {
            noteTitleEditText.setText(it)
        }
        acceptButton = fragment.findViewById(R.id.fragmentAcceptButton)
        acceptButton.setOnClickListener {
            viewModel.noteTitle.value = noteTitleEditText.text.toString()
            dismiss()
        }
        closeButton = fragment.findViewById(R.id.fragmentCloseButton)
        closeButton.setOnClickListener {
            dismiss()
        }
        editTagsButton = fragment.findViewById(R.id.fragmentManageTags)
        editTagsButton.setOnClickListener {
            viewModel.noteTitle.value = noteTitleEditText.text.toString()
            lifecycleScope.launch {
                withContext(IO) {
                    viewModel.saveFile(requireActivity().application)
                }
                viewModel.currentFileDBId.observe(viewLifecycleOwner) {
                    val intent = Intent(activity, AddTagsActivity::class.java)
                    intent.putExtra("id", it)
                    Log.i("myStartTags", "$it")
                    startActivity(intent)
                }
            }
        }
        return fragment
    }

}