package com.markdown_notepad.edit_activity_fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditActivityViewModel : ViewModel() {
    private var _isReadMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
    val isReadMode : LiveData<Boolean> = _isReadMode
    fun switchDisplayMode(isReadM : Boolean) {
        _isReadMode.value = isReadM
    }
}