package com.markdown_notepad.room

import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag

class Utilities(private val dao: FileDao) {

    fun tagFile(file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.setTagsForFile(ftr)
    }

    fun untagFile(file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.removeTagsFromFile(ftr)
    }

}