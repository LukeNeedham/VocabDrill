package com.lukeneedham.vocabdrill.data.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = VocabEntryTag.Table.NAME,
    foreignKeys = [
        ForeignKey(
            entity = VocabEntry::class,
            parentColumns = [VocabEntry.Column.ID],
            childColumns = [VocabEntryTag.Column.VOCAB_ENTRY_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = [Tag.Column.ID],
            childColumns = [VocabEntryTag.Column.TAG_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VocabEntryTag(
    @ColumnInfo(name = Column.VOCAB_ENTRY_ID) val vocabEntryId: Long,
    @ColumnInfo(name = Column.TAG_ID) val tagId: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Long = 0

    object Table {
        const val NAME = "vocab_entry_tags"
    }

    object Column {
        const val ID = "id"
        const val VOCAB_ENTRY_ID = "vocab_entry_id"
        const val TAG_ID = "tag_id"
    }
}
