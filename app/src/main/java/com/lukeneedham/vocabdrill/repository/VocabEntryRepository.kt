package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryDao
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.repository.conversion.toPersistenceModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry as VocabEntryPersistence
import com.lukeneedham.vocabdrill.domain.model.VocabEntry as VocabEntryDomain

class VocabEntryRepository(private val vocabEntryDao: VocabEntryDao) {
    /** Adds the entry, tags are added separately */
    fun addVocabEntry(wordA: String, wordB: String, languageId: Long): Single<Long> {
        val entry = VocabEntryPersistence(languageId, wordA, wordB)
        return vocabEntryDao.add(entry)
    }

    fun deleteVocabEntry(vocabEntryId: Long): Completable {
        return vocabEntryDao.deleteWithId(vocabEntryId)
    }

    fun observeVocabEntryForId(vocabEntryId: Long): Observable<VocabEntryDomain> {
        return vocabEntryDao.observeWithId(vocabEntryId).map { it.toDomainModel() }
    }

    fun requireVocabEntryForId(vocabEntryId: Long): Single<VocabEntryDomain> {
        return vocabEntryDao.getWithId(vocabEntryId).map { it.toDomainModel() }
    }

    fun observeAllVocabEntriesForLanguage(languageId: Long): Observable<List<VocabEntryDomain>> {
        return vocabEntryDao.observeAllForLanguage(languageId)
            .map { it.map { it.toDomainModel() } }
    }

    fun updateVocabEntry(vocabEntry: VocabEntryDomain): Completable {
        val persistence = vocabEntry.toPersistenceModel()
        return vocabEntryDao.update(persistence)
    }
}
