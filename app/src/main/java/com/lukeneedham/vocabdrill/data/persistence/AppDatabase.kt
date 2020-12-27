package com.lukeneedham.vocabdrill.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukeneedham.vocabdrill.data.persistence.AppDatabase.Companion.VERSION
import com.lukeneedham.vocabdrill.data.persistence.dao.LanguageDao
import com.lukeneedham.vocabdrill.data.persistence.dao.TagDao
import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryDao
import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryTagDao
import com.lukeneedham.vocabdrill.data.persistence.model.Language
import com.lukeneedham.vocabdrill.data.persistence.model.Tag
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntryTag

@Database(
    entities = [
        Language::class,
        VocabEntry::class,
        Tag::class,
        VocabEntryTag::class
    ],
    version = VERSION
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): LanguageDao
    abstract fun vocabEntryDao(): VocabEntryDao
    abstract fun tagDao(): TagDao
    abstract fun vocabEntryTagDao(): VocabEntryTagDao

    companion object {
        const val NAME = "database"

        const val VERSION = 1

        fun newInstance(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, NAME).build()
    }
}
