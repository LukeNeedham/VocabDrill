package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.LanguageDao
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.model.Language as LanguagePersistence
import com.lukeneedham.vocabdrill.domain.model.Language as LanguageDomain

class LanguageRepository(private val languageDao: LanguageDao) {
    fun getAllLanguages(): Single<List<LanguageDomain>> = languageDao.getAll()
        .map {
            it.map { it.toDomainModel() }
        }
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    fun addLanguage(languageProto: LanguageProto): Completable {
        val language = LanguagePersistence(languageProto.name, languageProto.country.alpha2Code)
        return languageDao.add(language)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }

    fun observeAllLanguages(): Observable<List<LanguageDomain>> = languageDao.observeAll().map {
        it.map { it.toDomainModel() }
    }
        .subscribeOn(RxSchedulers.database)
        .observeOn(RxSchedulers.main)

    fun requireLanguageForId(id: Long): Single<LanguageDomain> =
        languageDao.getWithId(id).map { it.toDomainModel() }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
}

