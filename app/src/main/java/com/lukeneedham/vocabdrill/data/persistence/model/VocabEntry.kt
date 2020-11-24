package com.lukeneedham.vocabdrill.data.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = VocabEntry.Table.NAME,
    foreignKeys = [
        ForeignKey(
            entity = Language::class,
            parentColumns = [Language.Column.ID],
            childColumns = [VocabEntry.Column.VOCAB_GROUP_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VocabEntry(
    @ColumnInfo(name = Column.VOCAB_GROUP_ID) val vocabGroupId: Long,
    @ColumnInfo(name = Column.WORD_A) val wordA: String,
    @ColumnInfo(name = Column.WORD_B) val wordB: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Long = 0

    object Table {
        const val NAME = "vocab_entries"
    }

    object Column {
        const val ID = "id"
        const val VOCAB_GROUP_ID = "vocab_group_id"
        const val WORD_A = "word_a"
        const val WORD_B = "word_b"
    }
}
