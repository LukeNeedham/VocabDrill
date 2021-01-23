package com.lukeneedham.vocabdrill.data.persistence.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = VocabSynonym.Table.NAME,
    foreignKeys = [
        ForeignKey(
            entity = VocabEntry::class,
            parentColumns = [VocabEntry.Column.ID],
            childColumns = [VocabSynonym.Column.VOCAB_ENTRY_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VocabSynonym(
    @ColumnInfo(name = Column.VOCAB_ENTRY_ID) val vocabEntryId: Long,
    @ColumnInfo(name = Column.TEXT) val word: String,
    /**
     * The type of the synonym. Either:
     * 1 - this is a synonym for [VocabEntry.wordA]
     * 2 - this is a synonym for [VocabEntry.wordB]
     */
    @ColumnInfo(name = Column.WORD_TYPE) val wordType: Int,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Long = 0

    object Table {
        const val NAME = "vocab_synonyms"
    }

    object Column {
        const val ID = "id"
        const val VOCAB_ENTRY_ID = "vocab_entry_id"
        const val TEXT = "word"
        const val WORD_TYPE = "word_type"
    }
}
