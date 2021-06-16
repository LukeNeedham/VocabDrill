package com.lukeneedham.vocabdrill.presentation.feature.language

import androidx.lifecycle.MutableLiveData
import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.EntryKey
import com.lukeneedham.vocabdrill.presentation.util.DisposingViewModel
import com.lukeneedham.vocabdrill.presentation.util.extension.toLiveData
import com.lukeneedham.vocabdrill.usecase.*
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LanguageViewModel(
    val languageId: Long,
    /* Vocab entry update usecases - unsaved changes must be committed before invoking these */
    private val addVocabEntry: AddVocabEntry,
    private val deleteVocabEntry: DeleteVocabEntry,
    private val addTagToVocabEntry: AddTagToVocabEntry,
    private val deleteTagFromVocabEntry: DeleteTagFromVocabEntry,
    private val updateVocabEntry: UpdateVocabEntry,
    /* Misc usecases */
    private val observeAllVocabEntryAndTagsForLanguage: ObserveAllVocabEntryAndTagsForLanguage,
    private val observeLanguage: ObserveLanguage,
    private val addNewTag: AddNewTag,
    private val calculateColorForNewTag: CalculateColorForNewTag,
    private val findTagNameMatches: FindTagNameMatches,
    private val deleteUnusedTags: DeleteUnusedTags,
    private val chooseTextColourForBackground: ChooseTextColourForBackground
) : DisposingViewModel() {

    private val languageNameMutableLiveData = MutableLiveData<String>()
    val languageNameLiveData = languageNameMutableLiveData.toLiveData()

    private val countryMutableLiveData = MutableLiveData<Country>()
    val countryLiveData = countryMutableLiveData.toLiveData()

    private val _entryKeys = MutableStateFlow<List<EntryKey>>(emptyList())
    val entryKeys = _entryKeys.asStateFlow()

    private val _selectedEntry = MutableStateFlow<EntryKey?>(null)
    val selectedEntry = _selectedEntry.asStateFlow()

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

    fun onEntrySelected(key: EntryKey) {
        _selectedEntry.value = key
    }

    fun addEntry(proto: VocabEntryProto) {
        val ignored = addVocabEntry(proto)
            .subscribe { }
    }

//    private fun requestTagMatches(entryItem: VocabEntryEditItem, tagName: String) {
//        fun updateTagSuggestionResult(suggestions: List<TagSuggestion>) {
//            entryReduxer.setTagSuggestionsResult(TagSuggestionResult(entryItem, suggestions))
//        }
//
//        findTagNameMatchesDisposable?.dispose()
//        val tagIdsAlreadyPresent =
//            entryItem.tagItems.filterIsInstance<TagItemProps.Existing>().map { it.data.id }
//        val findTagNameMatchesDisposable =
//            findTagNameMatches(languageId, tagName).subscribe { tags ->
//                val tagItems = tags.map {
//                    TagSuggestion.Existing(it, chooseTextColourForBackground(it.color))
//                }
//                val nonDuplicateTagItems = tagItems.toMutableList().apply {
//                    removeAll { it.data.id in tagIdsAlreadyPresent }
//                }.toList()
//                val showNewTag = tagName.isNotBlank() && tags.none { it.name == tagName }
//                if (!showNewTag) {
//                    updateTagSuggestionResult(nonDuplicateTagItems)
//                } else {
//                    // Current tag name doesn't currently exist - show it as a new suggestion
//                    disposables += calculateColorForNewTag(languageId).subscribe { color ->
//                        val newTag = TagSuggestion.Create(
//                            tagName,
//                            color,
//                            chooseTextColourForBackground(color)
//                        )
//                        val allTags = listOf(newTag) + nonDuplicateTagItems
//                        updateTagSuggestionResult(allTags)
//                    }
//                }
//            }
//        this.findTagNameMatchesDisposable = findTagNameMatchesDisposable
//        disposables += findTagNameMatchesDisposable
//    }
}
