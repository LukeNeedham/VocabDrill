package com.lukeneedham.vocabdrill.data.persistence.preferences

import android.content.Context
import androidx.core.content.edit
import com.lukeneedham.vocabdrill.presentation.util.extension.getSharedPreferences
import hu.autsoft.krate.Krate

class PreferencesDao(context: Context) : Krate {
    override val sharedPreferences = context.getSharedPreferences("Preferences")

    fun getTagCreationCount(languageId: Long): Int {
        val key = getTotalTagCreationKey(languageId)
        return sharedPreferences.getInt(key, 0)
    }

    fun setTagCreationCount(languageId: Long, count: Int) {
        val key = getTotalTagCreationKey(languageId)
        sharedPreferences.edit {
            putInt(key, count)
        }
    }

    private fun getTotalTagCreationKey(languageId: Long) = Key.TAG_CREATION_COUNT + languageId

    private object Key {
        /** Tracks the total number of tags which have ever been created for some language id */
        const val TAG_CREATION_COUNT = "TAG_CREATION_COUNT_"
    }
}
