package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.changeword

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG
import io.reactivex.rxkotlin.plusAssign

class ChangeVocabEntryWordViewModel(
    private val vocabEntryId: Long,
    private val wordType: WordType,
    private val vocabEntryRepository: VocabEntryRepository
) : DisposingViewModel() {

    private val loadVocabEntrySingle = vocabEntryRepository.requireVocabEntryForId(vocabEntryId)
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    private var newWord: String? = null

    private var loadedEntry: VocabEntry? = null

    private val wordMutableLiveData = MutableLiveData<String>()
    val wordLiveData = wordMutableLiveData.toLiveData()

    init {
        disposables += loadVocabEntrySingle.subscribe { entry ->
            loadedEntry = entry
            wordMutableLiveData.value = when (wordType) {
                WordType.WordA -> entry.wordA
                WordType.WordB -> entry.wordB
            }
        }
    }

    fun onWordChange(name: String) {
        this.newWord = name
    }

    @SuppressLint("CheckResult")
    fun updateWord() {
        val word = newWord
        if (word == null) {
            Log.e(TAG, "Cannot update entry - word is null")
            return
        }

        fun update(vocabEntry: VocabEntry) {
            val newEntry = when (wordType) {
                WordType.WordA -> vocabEntry.copy(wordA = word)
                WordType.WordB -> vocabEntry.copy(wordB = word)
            }
            vocabEntryRepository.updateVocabEntry(newEntry)
                .subscribeOn(RxSchedulers.database)
                .observeOn(RxSchedulers.main)
                .subscribe()
        }

        val loadedEntry = loadedEntry
        if (loadedEntry != null) {
            update(loadedEntry)
        } else {
            loadVocabEntrySingle.subscribe { language ->
                update(language)
            }
        }
    }
}
