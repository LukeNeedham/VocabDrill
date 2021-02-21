package com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion

import com.lukeneedham.vocabdrill.domain.model.Tag

sealed class TagSuggestion {
    abstract val color: Int
    abstract val textColor: Int
    abstract val name: String

    data class Existing(val data: Tag, override val textColor: Int) : TagSuggestion() {
        override val color = data.color
        override val name = data.name
    }

    class Create(override val name: String, override val color: Int, override val textColor: Int) :
        TagSuggestion()
}
