package com.lukeneedham.vocabdrill.domain.model

data class Language(
    val id: Long,
    val name: String,
    val country: Country
)
