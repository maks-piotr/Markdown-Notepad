package com.markdown_notepad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.markdown_notepad.R
import com.markdown_notepad.add_tags_features.AddTagAdapter
import com.markdown_notepad.room.AddTagFactory
import com.markdown_notepad.room.AddTagViewModel
import com.markdown_notepad.room.FileModelFactory
import com.markdown_notepad.room.FileViewModel
import com.markdown_notepad.room.MyPair
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.Tag

class AddTagsActivity : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var layoutAdapter: AddTagAdapter
    private var fileId: Int = 0
    private val addTagViewModel: AddTagViewModel by viewModels {
        AddTagFactory((application as MarkdownApplication).repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tags)

        fileId = intent.getIntExtra("id", 0)

        recyclerView = findViewById(R.id.recyclerViewAddTag)

        addTagViewModel.loadFile(fileId)
        createRecycler()
    }

    private fun createRecycler() {
        addTagViewModel.allTags.observe(this) {
            layoutAdapter = AddTagAdapter(it) { pair: MyPair ->
                addTagViewModel.toggleTag(pair)
            }
            recyclerView.adapter = layoutAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }
}