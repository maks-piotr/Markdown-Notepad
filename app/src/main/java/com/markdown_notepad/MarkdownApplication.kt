package com.markdown_notepad

import android.app.Application
import android.util.Log
import com.markdown_notepad.room.FileDatabase
import com.markdown_notepad.room.FileModelFactory
import com.markdown_notepad.room.FileViewModel
import com.markdown_notepad.room.Utilities
import androidx.activity.viewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MarkdownApplication : Application() {
    private val database by lazy { FileDatabase.getDatabase(this) }
    val repo by lazy { Utilities(database.fileDao()) }
    override fun onCreate() {

        super.onCreate()
        val scope = CoroutineScope(Job() + Dispatchers.Main)
        scope.launch { addInitialTags() }

    }
    private suspend fun addInitialTags() {
        val tagList = repo.allTags.first()
        Log.i("mylogs", "taglist $tagList")
        if (tagList.size < 4) {
            Log.i("mylogs", "adding initial tags" )
            repo.addTag("Favourite")
            repo.addTag("Work")
            repo.addTag("Ebook")
            repo.addTag("Reminders")
        }
    }
}