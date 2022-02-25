package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagSuggestion
import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Single

class GetTagSuggestions(
    private val calculateColorForNewTag: CalculateColorForNewTag,
    private val tagRepository: TagRepository
) {
    operator fun invoke(
        languageId: Long,
        /** The current tags on the entry in question */
        currentTags: List<Tag>,
        tagName: String
    ): Single<List<TagSuggestion>> {
        val createTagColorSingle = calculateColorForNewTag(languageId)
        val allTagsSingle = tagRepository.getAllForLanguage(languageId)

        return createTagColorSingle.zipWith(allTagsSingle) { createTagColor, allTags ->
            val matchedTags = allTags.filter { it.name.startsWith(tagName) }
            val uniqueTags = matchedTags - currentTags
            val existingTagSuggestions = uniqueTags.map { TagSuggestion.Existing(it) }

            val hasExactTagNameMatch = allTags.any { it.name == tagName }
            if (hasExactTagNameMatch) {
                return@zipWith existingTagSuggestions
            }

            val createSuggestion = TagSuggestion.Create(tagName, createTagColor)
            return@zipWith existingTagSuggestions + createSuggestion
        }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
