package com.markdown_notepad.room.entities

import androidx.room.*

@Entity(
    tableName = "file_tag",
    primaryKeys = ["fileId", "tagId"]
)
data class FileTagRelation(

    @ColumnInfo(name = "file_id")
    val fileId: Int,

    @ColumnInfo(name = "tag_id")
    val tagId: Int

)

data class FilesWithTags(

    @Embedded
    val file: File,

    @Relation(
        parentColumn = "file_id",
        entityColumn = "tag_id",
        associateBy = Junction(FileTagRelation::class)
    )

    val tags: List<Tag>

)

data class TagsWithFiles(

    @Embedded
    val tag: Tag,

    @Relation(
        parentColumn = "tag_id",
        entityColumn = "file_id",
        associateBy = Junction(FileTagRelation::class)
    )

    val files: List<File>

)
