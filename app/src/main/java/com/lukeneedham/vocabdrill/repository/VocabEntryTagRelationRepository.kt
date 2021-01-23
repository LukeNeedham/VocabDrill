package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.db.dao.TagDao
import com.lukeneedham.vocabdrill.data.persistence.db.dao.VocabEntryTagRelationDao
import com.lukeneedham.vocabdrill.data.persistence.db.model.VocabEntryTagRelation
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.extension.zip
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class VocabEntryTagRelationRepository(
    private val tagDao: TagDao,
    private val vocabEntryTagDao: VocabEntryTagRelationDao
) {
    fun addTagToVocabEntry(vocabEntryId: Long, tagId: Long): Single<Long> {
        val entryTag = VocabEntryTagRelation(vocabEntryId, tagId)
        return vocabEntryTagDao.add(entryTag)
    }

    fun deleteTagFromVocabEntry(vocabEntryId: Long, tagId: Long): Completable {
        return vocabEntryTagDao.delete(vocabEntryId, tagId)
    }

    fun observeAllTagsForVocabEntry(entryId: Long): Observable<List<Tag>> {
        return vocabEntryTagDao.observeAllForVocabEntry(entryId).switchMap { entryTags ->
            entryTags.map { tag ->
                tagDao.getWithId(tag.tagId).map { it.toDomainModel() }
            }.zip().toObservable()
        }
    }
}
