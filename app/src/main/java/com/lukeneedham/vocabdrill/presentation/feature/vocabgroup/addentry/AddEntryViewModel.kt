package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.addentry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData

class AddEntryViewModel(
    private val vocabGroupId: Long
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

    fun createNewEntry(): VocabEntryProto? {
        val wordA = wordA ?: return null
        val wordB = wordB ?: return null
        return VocabEntryProto(wordA, wordB, vocabGroupId)
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
