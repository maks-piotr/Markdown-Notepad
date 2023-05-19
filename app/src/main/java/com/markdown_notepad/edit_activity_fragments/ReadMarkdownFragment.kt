package com.markdown_notepad.edit_activity_fragments

import android.os.Bundle
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.markdown_notepad.R
import io.noties.markwon.Markwon
import org.commonmark.node.Node

/**
 * A [Fragment] subclass. User interface for reading rendered markdown
 */
class ReadMarkdownFragment : Fragment() {

    private lateinit var noteTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragment = inflater.inflate(R.layout.fragment_read_markdown, container, false)
        noteTextView = fragment.findViewById(R.id.noteTextView)
        val md: String = """
          # Hello!
          
          > a quote
          
          ```
          code block
          ```
          plain text
        """.trimIndent()

        // create markwon instance via builder method
        val markwon: Markwon = Markwon.builder(fragment.context).build()

        // parse markdown into commonmark representation
        val node: Node = markwon.parse(md)

        // render commonmark node
        val markdown: Spanned = markwon.render(node)

        // apply it to a TextView
        markwon.setParsedMarkdown(noteTextView, markdown)
        return fragment
    }
}