package com.markdown_notepad.room

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag
import kotlinx.coroutines.launch

class FileViewModel(private val repo: Utilities) : ViewModel() {
    var files: LiveData<List<File>> = repo.allFiles.asLiveData()
    var tags: LiveData<List<Tag>> = repo.allTags.asLiveData()
//    private var foundFile = MutableLiveData<File>()
//    private var taggedList = MutableLiveData<List<File>>()

    fun addFile(title: String, path: String) = viewModelScope.launch {
        repo.addFile(title, path)
    }

    fun addTag(name: String) = viewModelScope.launch {
        repo.addTag(name)
    }

    fun updateFile(file: File) = viewModelScope.launch {
        repo.updateFile(file)
    }

    fun tagFile(file: File, tag: Tag) = viewModelScope.launch {
        repo.tagFile(file, tag)
    }

    fun untagFile(file: File, tag: Tag) = viewModelScope.launch {
        repo.untagFile(file, tag)
    }

    fun getFileTags(file: File): LiveData<List<Tag>> {
        return repo.showFileTags(file).asLiveData()
    }

//    private fun getFileById(uid: Int) = viewModelScope.launch {
//        foundFile.value = repo.getFile(uid)
//    }

    fun getFile(uid: Int): LiveData<File> {
        var res = MutableLiveData<File>()
        viewModelScope.launch {
            res = repo.getFile(uid).asLiveData() as MutableLiveData<File>
        }
        return res
    }

    fun filterFilesByTags(tags: List<Tag>): LiveData<List<File>> {
        var res = MutableLiveData<List<File>>()
        viewModelScope.launch {
            res = repo.getFilesForTags(tags).asLiveData() as MutableLiveData<List<File>>
        }
        return res
    }

    fun filterFilesByTitle(search: String): LiveData<List<File>> {
        var res = MutableLiveData<List<File>>()
        viewModelScope.launch {
            res = repo.filterFiles(search).asLiveData() as MutableLiveData<List<File>>
        }
        return res
    }
}

class FileModelFactory(private val repo: Utilities) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(FileViewModel::class.java)) {
            return FileViewModel(repo) as T
        }
        throw java.lang.IllegalArgumentException("Unknown class for ViewModel")
    }
}