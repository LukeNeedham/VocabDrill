package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.dao.TagDao
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagProto
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.ColorExtraUtils
import io.reactivex.Completable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.model.Tag as TagPersistence

class TagRepository(
    private val tagDao: TagDao
) {
    fun addNewTag(proto: TagProto): Single<Tag> {
        val hexColor = ColorExtraUtils.toHex(proto.colour)
        val persistence = TagPersistence(proto.languageId, proto.name, hexColor)
        return tagDao.add(persistence).flatMap {
            tagDao.getWithId(it).map { it.toDomainModel() }
        }
    }

    fun deleteUnused(excludeTagIds: List<Long>): Completable {
        return tagDao.deleteAllUnused(excludeTagIds)
    }

    fun getAllForLanguage(languageId: Long): Single<List<Tag>> {
        return tagDao.getAllForLanguage(languageId).map {
            it.map { it.toDomainModel() }
        }
    }
}
