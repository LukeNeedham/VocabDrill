package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.TagDao
import com.lukeneedham.vocabdrill.data.persistence.dao.VocabEntryTagDao
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntryTag
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagProto
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.ColorExtraUtils
import com.lukeneedham.vocabdrill.util.extension.zip
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.model.Tag as TagPersistence

class VocabEntryTagsRepository(
    private val tagDao: TagDao,
    private val vocabEntryTagDao: VocabEntryTagDao
) {
    fun addTagToVocabEntry(vocabEntryId: Long, tagId: Long): Completable {
        val entryTag = VocabEntryTag(vocabEntryId, tagId)
        return vocabEntryTagDao.add(entryTag)
    }

    fun observeAllTagsForVocabEntry(entryId: Long): Observable<List<Tag>> {
        return vocabEntryTagDao.observeAllForVocabEntry(entryId).switchMap { entryTags ->
            entryTags.map {
                tagDao.getWithId(it.id).map { it.toDomainModel() }
            }.zip().toObservable()
        }
    }
}
