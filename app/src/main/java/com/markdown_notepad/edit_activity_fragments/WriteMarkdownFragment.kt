package com.markdown_notepad.edit_activity_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.markdown_notepad.R
import com.google.android.material.textfield.TextInputEditText
import com.markdown_notepad.focusAndShowKeyboard
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher


/**
 * A [Fragment] subclass. User interface for editing markdown
 */
class WriteMarkdownFragment : Fragment() {
    private lateinit var editText: TextInputEditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_write_markdown, container, false)
        editText = fragment.findViewById(R.id.noteEditText)
        // obtain Markwon instance
        val markwon = Markwon.create(fragment.context)
        // create editor
        val editor = MarkwonEditor.create(markwon)
        // set edit listener
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor))
        return fragment
    }

    override fun onStart() {
        super.onStart()
        editText.focusAndShowKeyboard()
    }
}