package com.markdown_notepad.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    val tagId: Int = 0,

    @ColumnInfo(name = "name")
    val tagName: String

)
