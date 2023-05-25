package com.markdown_notepad.room

import androidx.room.*
import com.markdown_notepad.room.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Query("SELECT * FROM files")
    fun getAllFiles(): Flow<List<File>>

    @Query("SELECT path FROM files WHERE uid = :uid LIMIT 1")
    suspend fun getPathById(uid: Int): String

    @Query("SELECT * FROM files WHERE uid = :fileId LIMIT 1")
    suspend fun getFileById(fileId: Int): File

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(vararg file: File)

    @Update
    suspend fun updateFiles(vararg file: File)

    @Delete
    suspend fun deleteFiles(vararg file: File)

    @Query("SELECT * FROM tags")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tags WHERE uid = :tagId LIMIT 1")
    suspend fun getTagById(tagId: Int): Tag

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(vararg tag: Tag)

    @Update
    suspend fun updateTags(vararg tag: Tag)

    @Delete
    suspend fun deleteTags(vararg tag: Tag)

    @Query("SELECT files.* FROM files " +
            "INNER JOIN file_tag ON file_tag.file_id = files.uid " +
            "WHERE file_tag.tag_id IN (:tagIdList)")
    fun getFilesWithTags(tagIdList: List<Int>): Flow<List<File>>

    @Query("SELECT tags.* FROM tags " +
            "INNER JOIN file_tag ON file_tag.tag_id = tags.uid " +
            "WHERE file_tag.file_id = :fileId")
    fun getTagsWithFiles(fileId: Int): Flow<List<Tag>>

    @Query("SELECT * FROM files " +
            "WHERE title LIKE '%' || :search || '%'")
    fun filterByTitle(search: String): Flow<List<File>>

    @Insert
    suspend fun setTagsForFile(vararg tagged: FileTagRelation)

    @Delete
    suspend fun removeTagsFromFile(vararg tagged: FileTagRelation)

}
