package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import com.lukeneedham.vocabdrill.util.extension.TAG

class AddVocabEntryViewModel(
    private val vocabGroupId: Long,
    private val vocabEntryRepository: VocabEntryRepository
) : ViewModel() {

    private var wordA: String? = null
    private var wordB: String? = null

    private val isValidPairMutableLiveData = MutableLiveData<Boolean>(false)
    val isValidPairLiveData = isValidPairMutableLiveData.toLiveData()

    fun onWordAChange(wordA: String) {
        this.wordA = wordA
        updateValidity()
    }

    fun onWordBChange(wordB: String) {
        this.wordB = wordB
        updateValidity()
    }

    fun createNewEntry() {
        val wordA = wordA
        if (wordA == null) {
            Log.e(TAG, "Entry cannot be created - word A is null")
            return
        }
        val wordB = wordB
        if (wordB == null) {
            Log.e(TAG, "Entry cannot be created - word B is null")
            return
        }
        val entry = VocabEntryProto(wordA, wordB, vocabGroupId)
        vocabEntryRepository.addVocabEntry(entry)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
            .subscribe()
    }

    private fun updateValidity() {
        isValidPairMutableLiveData.value = isValidPair()
    }

    private fun isValidPair(): Boolean {
        val wordA = wordA ?: return false
        val wordB = wordB ?: return false
        return wordA.isNotBlank() && wordB.isNotBlank()
    }
}
