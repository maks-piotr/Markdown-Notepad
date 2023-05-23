package com.markdown_notepad.room

import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag

class Utilities {

    fun tagFile(dao: FileDao, file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.setTagsForFile(ftr)
    }

    fun untagFile(dao: FileDao, file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.removeTagsFromFile(ftr)
    }

    fun filterByTags(dao: FileDao, tags: List<Tag>): List<File> {
        val files = mutableListOf<File>()
        for (tag in tags) {
            for (res in dao.getFilesWithTags(tag.tagId)) {
                files.add(dao.getFileById(res.fileId))
            }
        }
        return files.toSet().toList()
    }

    fun getFileTags(dao: FileDao, file: File): List<Tag> {
        val tags = mutableListOf<Tag>()
        for (res in dao.getTagsWithFiles(file.fileId)) {
            tags.add(dao.getTagById(res.tagId))
        }
        return tags.toList()
    }

}