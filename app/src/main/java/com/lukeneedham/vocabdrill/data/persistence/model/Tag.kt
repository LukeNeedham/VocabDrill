package com.lukeneedham.vocabdrill.data.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = Tag.Table.NAME,
    foreignKeys = [
        ForeignKey(
            entity = Language::class,
            parentColumns = [Language.Column.ID],
            childColumns = [Tag.Column.LANGUAGE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Tag(
    @ColumnInfo(name = Column.LANGUAGE_ID) val languageId: Long,
    @ColumnInfo(name = Column.NAME) val name: String,
    @ColumnInfo(name = Column.COLOR) val colorHex: String,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Long = 0

    object Table {
        const val NAME = "tags"
    }

    object Column {
        const val ID = "id"
        const val LANGUAGE_ID = "language_id"
        const val NAME = "name"
        const val COLOR = "color"
    }
}
