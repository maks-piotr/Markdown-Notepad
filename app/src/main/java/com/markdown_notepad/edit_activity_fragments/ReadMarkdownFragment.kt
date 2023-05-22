package com.markdown_notepad.edit_activity_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.markdown_notepad.R
import io.noties.markwon.Markwon

/**
 * A [Fragment] subclass. User interface for reading rendered markdown
 */
class ReadMarkdownFragment : Fragment() {

    private lateinit var noteTextView: TextView
    private lateinit var viewModel: EditActivityViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_read_markdown, container, false)
        // get ViewModel (this ViewModel is shared between EditActivity, ReadFragment, WriteFragment)
        viewModel = activity?.run {
            ViewModelProvider(this)[EditActivityViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        noteTextView = fragment.findViewById(R.id.noteTextView)
        viewModel.rawText.observe(viewLifecycleOwner) {
            val markwon = Markwon.create(requireActivity())
            markwon.setMarkdown(noteTextView, it)
        }
        return fragment
    }
}