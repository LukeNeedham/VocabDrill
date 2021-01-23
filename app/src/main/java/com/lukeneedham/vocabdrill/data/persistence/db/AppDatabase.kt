package com.lukeneedham.vocabdrill.data.persistence.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukeneedham.vocabdrill.data.persistence.db.AppDatabase.Companion.VERSION
import com.lukeneedham.vocabdrill.data.persistence.db.dao.LanguageDao
import com.lukeneedham.vocabdrill.data.persistence.db.dao.TagDao
import com.lukeneedham.vocabdrill.data.persistence.db.dao.VocabEntryDao
import com.lukeneedham.vocabdrill.data.persistence.db.dao.VocabEntryTagRelationDao
import com.lukeneedham.vocabdrill.data.persistence.db.model.Language
import com.lukeneedham.vocabdrill.data.persistence.db.model.Tag
import com.lukeneedham.vocabdrill.data.persistence.db.model.VocabEntry
import com.lukeneedham.vocabdrill.data.persistence.db.model.VocabEntryTagRelation

@Database(
    entities = [
        Language::class,
        VocabEntry::class,
        Tag::class,
        VocabEntryTagRelation::class
    ],
    version = VERSION
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): LanguageDao
    abstract fun vocabEntryDao(): VocabEntryDao
    abstract fun tagDao(): TagDao
    abstract fun vocabEntryTagDao(): VocabEntryTagRelationDao

    companion object {
        const val NAME = "database"

        const val VERSION = 1

        fun newInstance(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, NAME).build()
    }
}
