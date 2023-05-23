package com.markdown_notepad.room

import android.os.Bundle
import android.util.Log.d
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.markdown_notepad.R
import com.markdown_notepad.room.entities.File

class RoomActivity : AppCompatActivity() {

    lateinit var dao: FileDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        dao = Room.databaseBuilder(
            this, FileDatabase::class.java, "file_database"
        ).allowMainThreadQueries().build().fileDao()

        dao.insertFiles(
            File(pathToFile = "path/to/file", title = "qwerty"),
            File(pathToFile = "path/to/file", title = "asdf"),
            File(pathToFile = "path/to/file", title = "zxc"),
            File(pathToFile = "path/to/file", title = "vbn"),
            File(pathToFile = "path/to/file", title = "ghjk"),
            File(pathToFile = "path/to/file", title = "yuio")
        )

        val allFiles = dao.getAllFiles()

        d("forte", "allFiles size? ${allFiles.size}")

    }

}