package com.markdown_notepad.edit_activity_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.markdown_notepad.R
import com.example.markdown_notepad.databinding.FragmentWriteMarkdownBinding
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
    private lateinit var viewModel: EditActivityViewModel
    private lateinit var binding : FragmentWriteMarkdownBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate with DataBinding
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_write_markdown,
            container,
            false
        )
        val fragment = binding.root
        // get ViewModel (this ViewModel is shared between EditActivity, ReadFragment, WriteFragment)
        viewModel = activity?.run {
            ViewModelProvider(this)[EditActivityViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        binding.mainViewModel = viewModel
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
// custom BindingAdapter that sets cursor to the end of TextInput input
// it's called on data binding
@BindingAdapter("cursorPosition")
fun setCursorPosition(editText: TextInputEditText, value: String?) {
    value ?: return
    editText.setSelection(value.length)
}