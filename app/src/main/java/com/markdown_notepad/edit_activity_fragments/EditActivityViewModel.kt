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
import java.io.FileOutputStream
import java.util.*

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

            Log.i("URI EditViewModel",Uri.parse(databaseFile.pathToFile).toString());
           currentStorageFile =  Uri.parse(databaseFile.pathToFile).toFile();
           noteTitle.value = databaseFile.title ?: throw Exception("could not fetch note's title (File id = $id)")
           rawText.value = Uri.parse(databaseFile.pathToFile).toFile().readText()
//           rawText.value = applicationContext.contentResolver.openInputStream(Uri.parse(databaseFile.pathToFile))
//               ?.let { String(it.readBytes()) };
//           applicationContext.contentResolver.openFileDescriptor(
//               Uri.parse(databaseFile.pathToFile), "r"
//           )?.use {
//               FileInputStream(it.fileDescriptor).use { stream ->
//                   rawText.value = String(stream.readBytes())
//               }
//           }
       }
    }

    fun saveFile(applicationContext : Application) {
        if (currentStorageFile == null) {
//            val defaultDirectory = java.io.File(applicationContext.filesDir, DEFAULT_DIR)
//            if (!defaultDirectory.exists()) {
//                defaultDirectory.mkdirs()
//            }
//            var newFile : java.io.File = java.io.File(
//                defaultDirectory,
//                noteTitle.value +
//                        LocalDateTime.now().toString() +
//                        FILE_EXTENSION
//            )
//            while (newFile.exists()) {
//                newFile = java.io.File(
//                    defaultDirectory,
//                    noteTitle.value +
//                            LocalDateTime.now().toString() +
//                            "(${Random.nextInt()})$FILE_EXTENSION")
//            }
//            Log.i("mySave", "new: $newFile")
            currentStorageFile = File( applicationContext.getExternalFilesDir(null), UUID.randomUUID().toString())
            val rowId = CoroutineScope(IO).async {
                applicationContext.contentResolver.openFileDescriptor(
                    Uri.fromFile(currentStorageFile), "w"
                )?.use {
                    FileOutputStream(it.fileDescriptor).use {stream ->
                        stream.write(
                            rawText.value?.toByteArray()
                        )
                    }
                }
                return@async repo.addOneFile(noteTitle.value!!, Uri.fromFile(currentStorageFile).toString())
            }
            viewModelScope.launch {
                currentDatabaseFile = repo.getFileByRowId(rowId.await())
            }
        } else {
            CoroutineScope(IO).launch {
                Log.i("view model save",Uri.fromFile(currentStorageFile).toString());
                rawText.value?.toByteArray()?.let {
                    currentStorageFile?.writeBytes(it)
                };
//                applicationContext.contentResolver.openFileDescriptor(
//                    Uri.fromFile(currentStorageFile), "w"
//                )?.use {
//                    FileOutputStream(it.fileDescriptor).use {stream ->
//                        stream.write(
//                            rawText.value?.toByteArray()
//                        )
//                    }
//                }
                Log.i("mySave", "old: " + Uri.fromFile(currentStorageFile).toString())
//                if (!currentDatabaseFile!!.title.equals(noteTitle.value)) {
                currentDatabaseFile = com.markdown_notepad.room.entities.File(
                    currentDatabaseFile!!.fileId, currentDatabaseFile!!.pathToFile, noteTitle.value
                )
                repo.updateFile(currentDatabaseFile!!)
//                }
            }
        }
    }

    fun deleteNote() {
        if (currentStorageFile == null) return
        CoroutineScope(IO).launch {
            Log.i("myDelete", "old: " + currentStorageFile.toString())
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