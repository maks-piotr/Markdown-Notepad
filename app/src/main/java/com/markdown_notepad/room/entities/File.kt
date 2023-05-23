package com.markdown_notepad.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "files")
data class File(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    val fileId: Int = 0,

    @ColumnInfo(name = "path")
    val pathToFile:  String?,

    @ColumnInfo(name = "title")
    val title: String?

//    val modifyDate: Timestamp

)
