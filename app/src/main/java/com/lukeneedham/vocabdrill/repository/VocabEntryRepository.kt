package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryDao
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry as VocabEntryPersistence
import com.lukeneedham.vocabdrill.domain.model.VocabEntry as VocabEntryDomain

class VocabEntryRepository(private val vocabEntryDao: VocabEntryDao) {
    fun addVocabEntry(proto: VocabEntryProto): Completable {
        val entry = VocabEntryPersistence(proto.vocabGroupId, proto.wordA, proto.wordB)
        return vocabEntryDao.add(entry)
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }

    fun getAllVocabEntriesForVocabGroup(vocabGroupId: Long): Single<List<VocabEntryDomain>> {
        return vocabEntryDao.getAllForVocabGroup(vocabGroupId)
            .map { it.map { it.toDomainModel() } }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }

    fun observeAllVocabEntriesForVocabGroup(vocabGroupId: Long): Observable<List<VocabEntryDomain>> {
        return vocabEntryDao.observeAllForVocabGroup(vocabGroupId)
            .map { it.map { it.toDomainModel() } }
            .subscribeOn(RxSchedulers.database)
            .observeOn(RxSchedulers.main)
    }
}
