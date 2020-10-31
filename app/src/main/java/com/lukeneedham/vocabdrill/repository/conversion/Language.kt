package com.lukeneedham.vocabdrill.repository.conversion

import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.data.persistence.model.Language as LanguagePersistence
import com.lukeneedham.vocabdrill.domain.model.Language as LanguageDomain

fun LanguagePersistence.toDomainModel(): LanguageDomain {
    val country = Country(flagCountryAlpha2Code)
    return LanguageDomain(id, name, country)
}
