package com.markdown_notepad

import android.app.Application
import com.markdown_notepad.room.FileDatabase
import com.markdown_notepad.room.Utilities

class MarkdownApplication : Application() {
    private val database by lazy { FileDatabase.getDatabase(this) }
    val repo by lazy { Utilities(database.fileDao()) }
}