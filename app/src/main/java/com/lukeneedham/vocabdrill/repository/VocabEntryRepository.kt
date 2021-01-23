package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.db.dao.VocabEntryDao
import com.lukeneedham.vocabdrill.domain.model.VocabEntry
import com.lukeneedham.vocabdrill.repository.conversion.toPartialModel
import com.lukeneedham.vocabdrill.repository.conversion.toPersistenceModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.db.model.VocabEntry as VocabEntryPersistence

class VocabEntryRepository(private val vocabEntryDao: VocabEntryDao) {
    /** Adds the entry, tags are added separately */
    fun addVocabEntry(wordA: String, wordB: String, languageId: Long): Single<Long> {
        val entry = VocabEntryPersistence(languageId, wordA, wordB)
        return vocabEntryDao.add(entry)
    }

    fun deleteVocabEntry(vocabEntryId: Long): Completable {
        return vocabEntryDao.deleteWithId(vocabEntryId)
    }

    fun observeVocabEntryForId(vocabEntryId: Long): Observable<VocabEntry> {
        return vocabEntryDao.observeWithId(vocabEntryId).map { it.toPartialModel() }
    }

    fun requireVocabEntryForId(vocabEntryId: Long): Single<VocabEntry> {
        return vocabEntryDao.getWithId(vocabEntryId).map { it.toPartialModel() }
    }

    fun observeAllVocabEntriesForLanguage(languageId: Long): Observable<List<VocabEntry>> {
        return vocabEntryDao.observeAllForLanguage(languageId)
            .map { it.map { it.toPartialModel() } }
    }

    fun updateVocabEntry(vocabEntry: VocabEntry): Completable {
        val persistence = vocabEntry.toPersistenceModel()
        return vocabEntryDao.update(persistence)
    }
}
