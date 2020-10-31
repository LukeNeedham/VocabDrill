package com.lukeneedham.vocabdrill.data.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = VocabGroup.Table.NAME,
    foreignKeys = [
        ForeignKey(
            entity = Language::class,
            parentColumns = [Language.Column.ID],
            childColumns = [VocabGroup.Column.LANGUAGE_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VocabGroup(
    @ColumnInfo(name = Column.LANGUAGE_ID) val languageId: Long,
    @ColumnInfo(name = Column.NAME) val name: String,
    @ColumnInfo(name = Column.COLOUR) val colour: Int
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Long = 0

    object Table {
        const val NAME = "vocab_groups"
    }

    object Column {
        const val ID = "id"
        const val LANGUAGE_ID = "language_id"
        const val NAME = "name"
        const val COLOUR = "colour"
    }
}
