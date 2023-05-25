package com.markdown_notepad.room

import android.os.Bundle
import android.util.Log.d
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.markdown_notepad.R
import com.markdown_notepad.MarkdownApplication
import com.markdown_notepad.room.entities.File

class RoomActivity : AppCompatActivity() {

    private val fileViewModel: FileViewModel by viewModels {
        FileModelFactory((application as MarkdownApplication).repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        fileViewModel.addFile("qwerty", "path/to/file")
        fileViewModel.addFile("asdf", "path/to/file")
        fileViewModel.addFile("zxc", "path/to/file")
        fileViewModel.addFile("vbn", "path/to/file")
        fileViewModel.addFile("ghjk", "path/to/file")
        fileViewModel.addFile("yuio", "path/to/file")

//        Getting the file
        val file = fileViewModel.getFile(5)

        val allFiles = fileViewModel.files.value

        if (allFiles != null) {
            d("forte", "allFiles size? ${allFiles.size}")
        }

    }

}