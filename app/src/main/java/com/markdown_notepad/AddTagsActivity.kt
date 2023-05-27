package com.markdown_notepad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.markdown_notepad.R
import com.markdown_notepad.add_tags_features.AddTagAdapter
import com.markdown_notepad.room.FileModelFactory
import com.markdown_notepad.room.FileViewModel
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.Tag

class AddTagsActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutAdapter: AddTagAdapter
    private lateinit var allTags: List<Tag>
    private lateinit var usedTags: List<Tag>
    private lateinit var modifiedTags: MutableList<Tag>
    private var fileId: Int = 0
    private lateinit var file: File
    private val fileViewModel: FileViewModel by viewModels {
        FileModelFactory((application as MarkdownApplication).repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tags)

        fileId = intent.getIntExtra("id", 0)
        file = fileViewModel.getFile(fileId)!!

        fileViewModel.tags.observe(this) {
            allTags = ArrayList(it)
        }
        loadTags()

        layoutAdapter = AddTagAdapter(allTags, modifiedTags)
        recyclerView = findViewById(R.id.recyclerViewAddTag)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = layoutAdapter
    }

    fun updateTags(view: View) {
        for (tag in modifiedTags.minus(usedTags.toSet()))
            fileViewModel.tagFile(file, tag)
        for (tag in usedTags.minus(modifiedTags.toSet()))
            fileViewModel.untagFile(file, tag)

        loadTags()
    }

    private fun loadTags() {
        fileViewModel.getFileTags(file).observe(this) {
            usedTags = ArrayList(it)
        }
        modifiedTags = usedTags.toMutableList()
    }
}