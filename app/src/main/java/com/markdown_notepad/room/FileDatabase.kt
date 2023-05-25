package com.markdown_notepad.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.FileTagRelation
import com.markdown_notepad.room.entities.Tag

@Database(
    entities = [
        File::class,
        Tag::class,
        FileTagRelation::class],
    version = 1,
    exportSchema = false
)
abstract class FileDatabase : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {
        private var INSTANCE: FileDatabase? = null

        fun getDatabase(context: Context): FileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FileDatabase::class.java,
                    "file_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
