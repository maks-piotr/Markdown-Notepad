package com.markdown_notepad.room.entities

import androidx.room.*

@Entity(
    tableName = "file_tag",
    primaryKeys = ["file_id", "tag_id"]
)
data class FileTagRelation(

    @ColumnInfo(name = "file_id")
    val fileId: Int,

    @ColumnInfo(name = "tag_id")
    val tagId: Int

)
