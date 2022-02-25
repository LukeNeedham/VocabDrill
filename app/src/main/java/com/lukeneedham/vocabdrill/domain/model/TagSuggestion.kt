package com.lukeneedham.vocabdrill.domain.model

sealed class TagSuggestion {
    data class Existing(val data: Tag) : TagSuggestion()
    data class Create(val name: String, val color: Int) : TagSuggestion()
}
