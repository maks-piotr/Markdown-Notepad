package com.markdown_notepad.room

import androidx.room.*
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FilesWithTags
import com.markdown_notepad.room.entities.Tag
import com.markdown_notepad.room.entities.TagsWithFiles

@Dao
interface FileDao {

    @Query("SELECT * FROM files")
    fun getAllFiles(): List<File>

    @Query("SELECT path FROM files WHERE uid = :uid")
    fun getPathById(uid: Int): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFiles(vararg file: File)

    @Update
    fun updateFiles(vararg file: File)

    @Delete
    fun deleteFiles(vararg file: File)

    @Query("SELECT * FROM tags")
    fun getAllTags(): List<Tag>

    @Query("SELECT uid FROM tags WHERE name = :name LIMIT 1")
    fun getTagIdByName(tagName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTags(vararg tag: Tag)

    @Update
    fun updateTags(vararg tag: Tag)

    @Delete
    fun deleteTags(vararg tag: Tag)

    @Transaction
    @Query("SELECT * FROM File")
    fun getFilesWithTags(): List<FilesWithTags>

    @Transaction
    @Query("SELECT * FROM Tag")
    fun getTagsWithFiles(): List<TagsWithFiles>

    @Transaction
    @Query("SELECT * FROM :file")
    fun getTagsForFile(file: File): List<FilesWithTags>

    @Transaction
    @Query("SELECT * FROM :tag")
    fun getFilesForTag(tag: Tag): List<TagsWithFiles>

}
