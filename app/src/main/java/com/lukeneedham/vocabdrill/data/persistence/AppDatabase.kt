package com.lukeneedham.vocabdrill.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lukeneedham.vocabdrill.data.persistence.AppDatabase.Companion.VERSION
import com.lukeneedham.vocabdrill.data.persistence.dao.LanguageDao
import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryDao
import com.lukeneedham.vocabdrill.data.persistence.dao.VocabGroupDao
import com.lukeneedham.vocabdrill.data.persistence.model.Language
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry

@Database(
    entities = [
        Language::class,
        VocabEntry::class,
        VocabGroup::class
    ],
    version = VERSION
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): LanguageDao
    abstract fun vocabEntryDao(): VocabEntryDao
    abstract fun vocabGroupDao(): VocabGroupDao

    companion object {
        const val NAME = "database"

        const val VERSION = 1

        fun newInstance(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, NAME).build()
    }
}
