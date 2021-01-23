package com.lukeneedham.vocabdrill.repository.conversion

import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.data.persistence.db.model.Language as LanguagePersistence
import com.lukeneedham.vocabdrill.domain.model.Language as LanguageDomain

fun LanguagePersistence.toDomainModel(): LanguageDomain {
    val country = Country(flagCountryAlpha2Code)
    return LanguageDomain(id, name, country)
}

fun LanguageDomain.toPersistenceModel(): LanguagePersistence {
    val persistence = LanguagePersistence(name, country.alpha2Code)
    persistence.id = id
    return persistence
}
