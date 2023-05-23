package com.markdown_notepad.room

import androidx.room.*
import com.markdown_notepad.room.entities.*

@Dao
interface FileDao {

    @Query("SELECT * FROM files")
    fun getAllFiles(): List<File>

    @Query("SELECT path FROM files WHERE uid = :uid")
    fun getPathById(uid: Int): String

    @Query("SELECT * FROM files WHERE uid = :fileId LIMIT 1")
    fun getFileById(fileId: Int): File

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFiles(vararg file: File)

    @Update
    fun updateFiles(vararg file: File)

    @Delete
    fun deleteFiles(vararg file: File)

    @Query("SELECT * FROM tags")
    fun getAllTags(): List<Tag>

    @Query("SELECT * FROM tags WHERE uid = :tagId LIMIT 1")
    fun getTagById(tagId: Int): Tag

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTags(vararg tag: Tag)

    @Update
    fun updateTags(vararg tag: Tag)

    @Delete
    fun deleteTags(vararg tag: Tag)

//    @Transaction
    @Query("SELECT * FROM file_tag WHERE tag_id = :tagId")
    fun getFilesWithTags(tagId: Int): List<FileTagRelation>

//    @Transaction
    @Query("SELECT * FROM file_tag WHERE file_id = :fileId")
    fun getTagsWithFiles(fileId: Int): List<FileTagRelation>

    @Insert
    fun setTagsForFile(vararg tagged: FileTagRelation)

    @Delete
    fun removeTagsFromFile(vararg tagged: FileTagRelation)

}
