package com.markdown_notepad.edit_activity_fragments

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.markdown_notepad.room.Utilities
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.io.File
import java.util.*

class EditActivityViewModel(private val repo: Utilities) : ViewModel() {
    private var _isReadMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    val isReadMode : LiveData<Boolean> = _isReadMode
    val rawText : MutableLiveData<String> = MutableLiveData("")
    val noteTitle : MutableLiveData<String> = MutableLiveData("")
    private var currentStorageFile : File? = null
    private var currentDatabaseFile : com.markdown_notepad.room.entities.File? = null
    private var _currentFileDBId : MutableLiveData<Int> = MutableLiveData()
    val currentFileDBId : LiveData<Int> = _currentFileDBId

    fun switchDisplayMode(isReadM : Boolean) {
        _isReadMode.value = isReadM
    }

    fun initializeNewNote(defaultTitle : String) {
        noteTitle.value = defaultTitle
    }

    fun loadFile(id : Int) {
       viewModelScope.launch {
            val databaseFile = repo.getFile(id) as com.markdown_notepad.room.entities.File? ?: throw Exception("File (id = $id) not found in database")
            currentDatabaseFile = databaseFile
            Log.i("editAVM", "load" + Uri.parse(databaseFile.pathToFile).toString())
            currentStorageFile =  Uri.parse(databaseFile.pathToFile).toFile()
            noteTitle.value = databaseFile.title ?: throw Exception("could not fetch note's title (File id = $id)")
            rawText.value = Uri.parse(databaseFile.pathToFile).toFile().readText()
            _currentFileDBId.value = currentDatabaseFile!!.fileId
       }
    }
    fun saveFile(applicationContext : Application) {
        if (currentStorageFile == null) {
            val id = CoroutineScope(IO).async {
                currentStorageFile = createNewFileForStorage(applicationContext)
                rawText.value?.toString()?.let {
                    currentStorageFile!!.writeText(it)
                }
                Log.i("editAVM", "save new: " + Uri.fromFile(currentStorageFile).toString())
                repo.addOneFile(noteTitle.value!!, Uri.fromFile(currentStorageFile).toString())
            }
            viewModelScope.launch {
                currentDatabaseFile = repo.getFileByRowId(id.await())
                _currentFileDBId.value = currentDatabaseFile!!.fileId
            }
        } else {
            CoroutineScope(IO).launch {
                rawText.value?.toString()?.let {
                    currentStorageFile!!.writeText(it)
                }
                Log.i("editAVM", "save old: " + Uri.fromFile(currentStorageFile).toString())
                currentDatabaseFile = com.markdown_notepad.room.entities.File(
                    currentDatabaseFile!!.fileId, currentDatabaseFile!!.pathToFile, noteTitle.value
                )
                repo.updateFile(currentDatabaseFile!!)
            }
        }
    }

    private fun createNewFileForStorage(applicationContext: Application) : File {
        return File( applicationContext.getExternalFilesDir(null), UUID.randomUUID().toString())
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
}

class EditActivityViewModelFactory(private val repo: Utilities) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(EditActivityViewModel::class.java)) {
            return EditActivityViewModel(repo) as T
        }
        throw java.lang.IllegalArgumentException("Unknown class for ViewModel")
    }
}