package com.markdown_notepad.edit_activity_fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditActivityViewModel : ViewModel() {
    private var _isReadMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    val isReadMode : LiveData<Boolean> = _isReadMode
    val rawText : MutableLiveData<String> = MutableLiveData("")
    val noteTitle : MutableLiveData<String> = MutableLiveData("")

    fun switchDisplayMode(isReadM : Boolean) {
        _isReadMode.value = isReadM
    }

    fun initializeNewNote(defaultTitle : String = "Note") {
        noteTitle.value = defaultTitle
    }
}