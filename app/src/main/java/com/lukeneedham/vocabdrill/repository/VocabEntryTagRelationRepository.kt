package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.TagDao
import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryTagRelationDao
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntryTagRelation
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.extension.zip
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

    fun observeAllTagsForVocabEntry(entryId: Long): Observable<List<Tag>> {
        return vocabEntryTagDao.observeAllForVocabEntry(entryId).switchMap { entryTags ->
            entryTags.map { tag ->
                tagDao.getWithId(tag.tagId).map { it.toDomainModel() }
            }.zip().toObservable()
        }
    }
}
