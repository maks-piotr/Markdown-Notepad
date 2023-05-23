package com.markdown_notepad.room

import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag

class Utilities(private val dao: FileDao) {

    fun addFile(title: String, path: String) {
        val file = File(pathToFile = path, title = title)
        dao.insertFiles(file)
    }

    fun tagFile(file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.setTagsForFile(ftr)
    }

    fun untagFile(file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.removeTagsFromFile(ftr)
    }

    fun getFile(uid: Int): File {
        return dao.getFileById(uid)
    }

    fun showFileTags(file: File): List<Tag> {
        return dao.getTagsWithFiles(file.fileId)
    }

}