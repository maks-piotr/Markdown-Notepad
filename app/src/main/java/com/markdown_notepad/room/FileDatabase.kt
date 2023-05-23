package com.markdown_notepad.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag

@Database(entities = [File::class, Tag::class, FileTagRelation::class], version = 1)
abstract class FileDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

}
