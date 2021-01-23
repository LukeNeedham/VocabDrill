package com.lukeneedham.vocabdrill.repository

import com.lukeneedham.vocabdrill.data.persistence.db.dao.TagDao
import com.lukeneedham.vocabdrill.data.persistence.preferences.PreferencesDao
import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.domain.model.TagProto
import com.lukeneedham.vocabdrill.repository.conversion.toDomainModel
import com.lukeneedham.vocabdrill.util.ColorExtraUtils
import io.reactivex.Completable
import io.reactivex.Single
import com.lukeneedham.vocabdrill.data.persistence.db.model.Tag as TagPersistence

class TagRepository(
    private val tagDao: TagDao,
    private val preferencesDao: PreferencesDao
) {
    fun addNewTag(proto: TagProto): Single<Tag> {
        val hexColor = ColorExtraUtils.toHex(proto.colour)
        val languageId = proto.languageId
        val persistence = TagPersistence(languageId, proto.name, hexColor)
        return tagDao.add(persistence)
            .doOnSuccess {
                incrementTagCreationCount(languageId)
            }
            .flatMap {
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

    fun getTagCreationCount(languageId: Long) = preferencesDao.getTagCreationCount(languageId)

    private fun incrementTagCreationCount(languageId: Long) {
        val oldCount = preferencesDao.getTagCreationCount(languageId)
        val newCount = oldCount + 1
        preferencesDao.setTagCreationCount(languageId, newCount)
    }
}
