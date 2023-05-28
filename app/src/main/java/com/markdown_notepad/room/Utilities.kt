package com.markdown_notepad.room

import androidx.annotation.WorkerThread
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag
import kotlinx.coroutines.flow.Flow

class Utilities(private val dao: FileDao) {
    val allFiles = dao.getAllFiles()
    val allTags = dao.getAllTags()

//    @WorkerThread
    fun showFileTags(file: File): Flow<List<Tag>> {
        return dao.getTagsWithFiles(file.fileId)
    }

//    @WorkerThread
    fun getFilesForTags(tags: List<Tag>): Flow<List<File>> {
        val tagsIdList = tags.map { it.tagId }
        return dao.getFilesWithTags(tagsIdList)
    }

    fun filterFiles(search: String): Flow<List<File>> {
        return dao.filterByTitle(search)
    }


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
    suspend fun getFile(uid: Int): Flow<File> {
        return dao.getFileById(uid)
    }

    //returns rowid - SQLite internal primary key
    @WorkerThread
    suspend fun getFileByRowId(rowId: Long) : File {
        return dao.getFileByRowId(rowId)
    }

    @WorkerThread
    suspend fun updateFile(file: File) {
        dao.updateFiles(file)
    }

    @WorkerThread
    suspend fun addOneFile(title: String, path: String) : Long {
        val file = File(pathToFile = path, title = title)
        return dao.insertOneFile(file);
    }

    @WorkerThread
    suspend fun deleteFile(file : File) {
        dao.deleteFiles(file)
    }
}