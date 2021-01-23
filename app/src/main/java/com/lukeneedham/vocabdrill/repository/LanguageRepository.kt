package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.db.dao.LanguageDao
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.repository.conversion.toPersistenceModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.db.model.Language as LanguagePersistence
import com.lukeneedham.vocabdrill.domain.model.Language as LanguageDomain

class LanguageRepository(private val languageDao: LanguageDao) {
    fun getAllLanguages(): Single<List<LanguageDomain>> = languageDao.getAll()
        .map {
            it.map { it.toDomainModel() }
        }

    fun addLanguage(languageProto: LanguageProto): Completable {
        val language = LanguagePersistence(languageProto.name, languageProto.country.alpha2Code)
        return languageDao.add(language)
    }

    fun deleteLanguage(languageId: Long): Completable {
        return languageDao.deleteWithId(languageId)
    }

    fun updateLanguage(language: LanguageDomain): Completable {
        val languagePersistence = language.toPersistenceModel()
        return languageDao.update(languagePersistence)
    }

    fun observeAllLanguages(): Observable<List<LanguageDomain>> = languageDao.observeAll().map {
        it.map { it.toDomainModel() }
    }

    fun requireLanguageForId(id: Long): Single<LanguageDomain> =
        languageDao.getWithId(id).map { it.toDomainModel() }

    fun observeLanguageForId(id: Long): Observable<LanguageDomain> =
        languageDao.observeWithId(id).map { it.toDomainModel() }

    fun getLanguageForId(id: Long): Single<LanguageDomain> =
        languageDao.getWithId(id).map { it.toDomainModel() }
}

