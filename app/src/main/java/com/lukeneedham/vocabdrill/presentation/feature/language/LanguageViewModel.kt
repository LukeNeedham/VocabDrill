package com.lukeneedham.vocabdrill.presentation.feature.language

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.EntryKey
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.OnceEvent
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.*
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel(
    val languageId: Long,
    /* Vocab entry update usecases - unsaved changes must be committed before invoking these */
    private val addVocabEntry: AddVocabEntry,
    /* Misc usecases */
    private val observeAllVocabEntryAndTagsForLanguage: ObserveAllVocabEntryAndTagsForLanguage,
    private val observeLanguage: ObserveLanguage,
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val _entryKeys = MutableStateFlow<List<EntryKey>>(emptyList())
    val entryKeys = _entryKeys.asStateFlow()

    private val _selectedEntry = MutableStateFlow<EntryKey?>(null)
    val selectedEntry = _selectedEntry.asStateFlow()

    var createWordA by mutableStateOf("")
        private set
    var createWordB by mutableStateOf("")
        private set

    var scrollToBottomEvent by mutableStateOf<OnceEvent?>(null)
        private set

    init {
        disposables += observeLanguage(languageId).subscribe { language ->
            languageNameMutableLiveData.value = language.name
            countryMutableLiveData.value = language.country
        }

        disposables += observeAllVocabEntryAndTagsForLanguage(languageId).subscribe {
            val existingKeys = it.map { EntryKey.Existing(it) }
            val allKeys = existingKeys + EntryKey.Create
            _entryKeys.value = allKeys
        }
    }

    fun getLearnSet(): LearnSet {
        val entries =
            entryKeys.value.filterIsInstance<EntryKey.Existing>().map { it.taggedEntry.entry }
        return LearnSet(entries)
    }

    fun onCreateEntrySelected() {
        onEntrySelected(EntryKey.Create)
        scrollToBottomEvent = OnceEvent()
    }

    fun onEntrySelected(key: EntryKey) {
        _selectedEntry.value = key
    }

    fun onCreateWordAChange(word: String) {
        createWordA = word
    }

    fun onCreateWordBChange(word: String) {
        createWordB = word
    }

    fun onEntryAdded() {
        val newEntry = VocabEntryProto(createWordA, createWordB, languageId, emptyList())
        val ignored = addVocabEntry(newEntry).subscribe(
            {
                createWordA = ""
                createWordB = ""
                scrollToBottomEvent = OnceEvent()
            },
            {
                Log.e(TAG, "Failed to add new entry", it)
            }
        )
    }
}
