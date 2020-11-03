package com.lukeneedham.vocabdrill.usecase

import com.lukeneedham.vocabdrill.domain.model.Country

class ExtractFlagColoursFromCountry(
    private val loadFlagVectorForCountry: LoadFlagVectorForCountry
) {
    operator fun invoke(country: Country): List<Int> {
        val drawable = loadFlagVectorForCountry(country)
        return drawable.allPathModels.map {
            it.fillColor
        }.distinct()
    }
}
