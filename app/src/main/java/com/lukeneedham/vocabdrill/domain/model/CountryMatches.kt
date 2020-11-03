package com.lukeneedham.vocabdrill.domain.model

sealed class CountryMatches {
    object Searching : CountryMatches()
    data class Found(val countries: List<Country>) : CountryMatches()
}
