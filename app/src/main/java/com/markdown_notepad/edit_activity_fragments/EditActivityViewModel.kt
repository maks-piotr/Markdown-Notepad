package com.markdown_notepad.edit_activity_fragments

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.markdown_notepad.room.Utilities
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDateTime
import kotlin.random.Random

class EditActivityViewModel(private val repo: Utilities) : ViewModel() {
    private var _isReadMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    val isReadMode : LiveData<Boolean> = _isReadMode
    val rawText : MutableLiveData<String> = MutableLiveData("")
    val noteTitle : MutableLiveData<String> = MutableLiveData("")
    private var currentStorageFile : java.io.File? = null
    private var currentDatabaseFile : com.markdown_notepad.room.entities.File? = null

    fun switchDisplayMode(isReadM : Boolean) {
        _isReadMode.value = isReadM
    }

    fun initializeNewNote(defaultTitle : String) {
        noteTitle.value = defaultTitle
    }

    fun loadFile(applicationContext : Application, id : Int) {
       viewModelScope.launch {
           val databaseFile = repo.getFile(id) as com.markdown_notepad.room.entities.File?
               ?: throw Exception("File (id = $id) not found in database")
           currentDatabaseFile = databaseFile
           currentStorageFile = java.io.File(databaseFile.pathToFile ?: throw Exception("could not fetch file's path (File id = $id)"))
           noteTitle.value = databaseFile.title ?: throw Exception("could not fetch note's title (File id = $id)")
           applicationContext.contentResolver.openFileDescriptor(
               Uri.fromFile(currentStorageFile), "r"
           )?.use {
               FileInputStream(it.fileDescriptor).use { stream ->
                   rawText.value = String(stream.readBytes())
               }
           }
       }
    }

    fun saveFile(applicationContext : Application) {
        if (currentStorageFile == null) {
            val newFile = createNewFileInStorage(applicationContext)
            currentStorageFile = newFile
            val rowId = saveFileForRowIdAsync(applicationContext, newFile)
            viewModelScope.launch {
                currentDatabaseFile = repo.getFileByRowId(rowId.await())
            }
        } else {
            CoroutineScope(IO).launch {
                applicationContext.contentResolver.openFileDescriptor(
                    Uri.fromFile(currentStorageFile), "w"
                )?.use {
                    FileOutputStream(it.fileDescriptor).use {stream ->
                        stream.write(
                            rawText.value?.toByteArray()
                        )
                    }
                }
                Log.i("markdownFileIO", "old: " + currentStorageFile.toString())
                if (!currentDatabaseFile!!.title.equals(noteTitle.value)) {
                    currentDatabaseFile = com.markdown_notepad.room.entities.File(
                        currentDatabaseFile!!.fileId, currentDatabaseFile!!.pathToFile, noteTitle.value
                    )
                    repo.updateFile(currentDatabaseFile!!)
                }
            }
        }
    }
    private fun createNewFileInStorage(applicationContext: Application) : java.io.File {
        val defaultDirectory = java.io.File(applicationContext.filesDir, DEFAULT_DIR)
        if (!defaultDirectory.exists()) {
            defaultDirectory.mkdirs()
        }
        var newFile : java.io.File = java.io.File(
            defaultDirectory,
            noteTitle.value +
                    LocalDateTime.now().toString() +
                    FILE_EXTENSION
        )
        while (newFile.exists()) {
            newFile = java.io.File(
                defaultDirectory,
                noteTitle.value +
                        LocalDateTime.now().toString() +
                        "(${Random.nextInt()})$FILE_EXTENSION")
        }
        return newFile
    }
    private fun saveFileForRowIdAsync(applicationContext: Application, newFile : java.io.File) = CoroutineScope(IO).async {
        applicationContext.contentResolver.openFileDescriptor(
            Uri.fromFile(newFile), "w"
        )?.use {
            FileOutputStream(it.fileDescriptor).use {stream ->
                stream.write(
                    rawText.value?.toByteArray()
                )
            }
        }
        Log.i("markdownFileIO", "new: $newFile")
        return@async repo.addOneFile(noteTitle.value!!, newFile.canonicalPath)
    }
    suspend fun prepareFileIdAsync(applicationContext : Application) : Int {
        if (currentDatabaseFile != null) {
            return currentDatabaseFile!!.fileId
        }
        val newFile = createNewFileInStorage(applicationContext)
        currentStorageFile = newFile
        val rowId = saveFileForRowIdAsync(applicationContext, newFile)
        val file = viewModelScope.async {
            return@async repo.getFileByRowId(rowId.await())
        }
        val newDBFile = file.await()
        currentDatabaseFile = newDBFile
        return newDBFile.fileId
    }

    fun deleteNote() {
        if (currentStorageFile == null) return
        CoroutineScope(IO).launch {
            currentStorageFile?.delete()
            currentDatabaseFile?.let { repo.deleteFile(it) }
            currentStorageFile = null
            currentDatabaseFile = null
        }
    }
    companion object {
        const val DEFAULT_DIR = "internal"
        private const val FILE_EXTENSION = ".md"
    }
}

class EditActivityViewModelFactory(private val repo: Utilities) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            return EditActivityViewModel(repo) as T
        }
        throw java.lang.IllegalArgumentException("Unknown class for ViewModel")
    }
}