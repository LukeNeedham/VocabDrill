package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.domain.model.VocabEntryAndTags
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.TagItem
import com.lukeneedham.vocabdrill.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExistingEntryItemViewModel(
    private val entryId: Long,
    observeVocabEntryAndTags: ObserveVocabEntryAndTagsForId,
    private val updateVocabEntry: UpdateVocabEntry,
//    private val deleteVocabEntry: DeleteVocabEntry,
//    private val addTagToVocabEntry: AddTagToVocabEntry,
//    private val deleteTagFromVocabEntry: DeleteTagFromVocabEntry,
) {
    private var entryAndTags: VocabEntryAndTags? = null

    private val _wordA = MutableStateFlow<String>("")
    val wordA = _wordA.asStateFlow()

    private val _wordB = MutableStateFlow<String>("")
    val wordB = _wordB.asStateFlow()

    private val _tagItems = MutableStateFlow<List<TagItem>>(emptyList())
    val tagItems = _tagItems.asStateFlow()

    private val _isSelected = MutableStateFlow<Boolean>(false)
    val isSelected = _isSelected.asStateFlow()

    init {
        observeVocabEntryAndTags(entryId).subscribe {
            onVocabEntryLoad(it)
        }
    }

    fun setSelected(isSelected: Boolean) {
        _isSelected.value = isSelected
    }

//    fun onWordAChange(text: String) {
//        val entry = getEntry() ?: return
//        val newEntry = entry.copy(wordA = text)
//        updateVocabEntry(newEntry).subscribe()
//        // For immediate UI update
//        _wordA.value = text
//    }

    private fun getEntry() = entryAndTags?.entry

    private fun onVocabEntryLoad(entryAndTags: VocabEntryAndTags) {
        this.entryAndTags = entryAndTags
        val entry = entryAndTags.entry
        _wordA.value = entry.wordA
        _wordB.value = entry.wordB
        _tagItems.value = entryAndTags.tags.map { TagItem.Existing(it) }
    }
}
