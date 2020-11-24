package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.VocabGroupDao
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class VocabGroupRepository(
    private val vocabGroupDao: VocabGroupDao
) {
    fun addVocabGroup(proto: VocabGroupProto): Completable {
        val group = VocabGroupPersistence(proto.languageId, proto.name, proto.colour)
        return vocabGroupDao.add(group)
    }

    fun updateVocabGroup(vocabGroup: VocabGroupDomain): Completable {
        val persistence = vocabGroup.toPersistenceModel()
        return vocabGroupDao.update(persistence)
    }

    fun deleteVocabGroup(vocabGroupId: Long): Completable {
        return vocabGroupDao.deleteWithId(vocabGroupId)
    }

    fun requireVocabGroupForId(id: Long): Single<VocabGroupDomain> {
        return vocabGroupDao.getWithId(id).map { it.toDomainModel() }
    }

    fun observeVocabGroupForId(id: Long): Observable<VocabGroupDomain> {
        return vocabGroupDao.observeWithId(id).map { it.toDomainModel() }
    }

    fun getAllVocabGroupsForLanguage(languageId: Long): Single<List<VocabGroupDomain>> {
        return vocabGroupDao.getAllForLanguage(languageId).map { it.map { it.toDomainModel() } }
    }

    fun observeAllVocabGroupsForLanguage(languageId: Long): Observable<List<VocabGroupDomain>> {
        return vocabGroupDao.observeAllForLanguage(languageId).map { it.map { it.toDomainModel() } }
    }
}
