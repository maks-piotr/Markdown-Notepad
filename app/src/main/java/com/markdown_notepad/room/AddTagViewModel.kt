package com.markdown_notepad.room

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.Tag
import kotlinx.coroutines.launch

class AddTagViewModel(private val repo: Utilities) : ViewModel() {
    var allTags: MutableLiveData<MutableList<MyPair>> = MutableLiveData(mutableListOf())
    var foundFile = MutableLiveData<File>()

    fun loadFile(id: Int) = viewModelScope.launch {
        foundFile.value = repo.getFile(id)
        repo.showFileTags(foundFile.value!!).collect { usedTags ->
            repo.allTags.collect { at ->
                var tempList = mutableListOf<MyPair>()
                for (tag in at) {
                    if (tag in usedTags)
                        tempList.add(MyPair(tag, true))
                    else
                        tempList.add(MyPair(tag, false))
                }
                allTags.value = tempList
                Log.i("USED TAGS", allTags.value.toString())
            }
        }
    }

    private fun addTag(pair: MyPair) = viewModelScope.launch {
        foundFile.value?.let { repo.tagFile(it, pair.first) }
        pair.second = true
    }

    private fun removeTag(pair: MyPair) = viewModelScope.launch {
        foundFile.value?.let { repo.untagFile(it, pair.first) }
        pair.second = false
    }

    fun toggleTag(pair: MyPair) {
        if (pair.second)
            removeTag(pair)
        else addTag(pair)
    }

}

class MyPair(var first: Tag, var second: Boolean)

class AddTagFactory(private val repo: Utilities) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(AddTagViewModel::class.java)) {
            return AddTagViewModel(repo) as T
        }
        throw java.lang.IllegalArgumentException("Unknown class for ViewModel")
    }
}