package com.lukeneedham.vocabdrill.presentation.feature.language

import com.lukeneedham.vocabdrill.domain.model.TagProto
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.LoadingState
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.util.InvalidInputException
import com.lukeneedham.vocabdrill.presentation.util.extension.toObservable
import io.reactivex.subjects.BehaviorSubject

class VocabEntryItemCreateViewModel(private val languageId: Long) {

    private var item: VocabEntryItem.Create? = null

    private var wordA: String? = null
    private var wordB: String? = null

    private val tagsSubject = BehaviorSubject.createDefault<List<TagProto>>(emptyList())
    val tagsObservable = tagsSubject.toObservable()

    private val saveButtonEnabledSubject = BehaviorSubject.createDefault(false)
    val saveButtonEnabledObservable = saveButtonEnabledSubject.toObservable()

    private val loadingStateSubject = BehaviorSubject.createDefault(LoadingState.Ready)
    val loadingStateObservable = loadingStateSubject.toObservable().distinctUntilChanged()

    fun onWordAChanged(newWord: String) {
        wordA = newWord
    }

    fun onWordBChanged(newWord: String) {
        wordB = newWord
    }

//    @SuppressLint("CheckResult")
//    fun saveEntry() {
//        updateState(LoadingState.Saving)
//        updateSaveButtonEnabled()
//        val proto = createNewEntry()
//        addVocabEntry(proto).subscribe {
//            updateState(LoadingState.Ready)
//        }
//    }

    private fun createNewEntry(): VocabEntryProto {
        val wordA = wordA ?: throw InvalidInputException("wordA", wordA)
        val wordB = wordB ?: throw InvalidInputException("wordB", wordB)
        val languageId = item?.languageId
        languageId ?: throw InvalidInputException("languageId", languageId)
        return VocabEntryProto(wordA, wordB, languageId, emptyList())
    }
}
