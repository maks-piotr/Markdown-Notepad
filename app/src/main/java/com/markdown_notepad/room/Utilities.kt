package com.markdown_notepad.room

import androidx.annotation.WorkerThread
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag
import kotlinx.coroutines.flow.Flow

class Utilities(private val dao: FileDao) {

    @WorkerThread
    suspend fun addFile(title: String, path: String) {
        val file = File(pathToFile = path, title = title)
        dao.insertFiles(file)
    }

    @WorkerThread
    suspend fun addTag(name: String) {
        val tag = Tag(tagName = name)
        dao.insertTags(tag)
    }

    @WorkerThread
    suspend fun tagFile(file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.setTagsForFile(ftr)
    }

    @WorkerThread
    suspend fun untagFile(file: File, tag: Tag) {
        val ftr = FileTagRelation(file.fileId, tag.tagId)
        dao.removeTagsFromFile(ftr)
    }

    @WorkerThread
    suspend fun getFile(uid: Int): File {
        return dao.getFileById(uid)
    }

    @WorkerThread
    suspend fun updateFile(file: File) {
        dao.updateFiles(file)
    }

    @WorkerThread
    fun getAllFiles(): Flow<List<File>> {
        return dao.getAllFiles()
    }

    @WorkerThread
    fun getAllTags(): Flow<List<Tag>> {
        return dao.getAllTags()
    }

    @WorkerThread
    fun showFileTags(file: File): Flow<List<Tag>> {
        return dao.getTagsWithFiles(file.fileId)
    }

    @WorkerThread
    fun getFilesForTags(tags: List<Tag>): Flow<List<File>> {
        val tagsIdList = tags.map { it.tagId }
        return dao.getFilesWithTags(tagsIdList)
    }

}