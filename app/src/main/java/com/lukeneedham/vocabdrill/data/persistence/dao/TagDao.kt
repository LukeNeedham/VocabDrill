package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lukeneedham.vocabdrill.data.persistence.model.Tag
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntryTagRelation
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TagDao {

    @Insert
    fun add(tag: Tag): Single<Long>

    @Query("SELECT * FROM ${Tag.Table.NAME}")
    fun getAll(): Single<List<Tag>>

    /** If a [Tag] is not referenced in any [VocabEntryTagRelation], it is never used. */
    @Query(
        """
        DELETE FROM ${Tag.Table.NAME}
        WHERE ${Tag.Column.ID} NOT IN (
            SELECT DISTINCT ${VocabEntryTagRelation.Column.TAG_ID} 
            FROM ${VocabEntryTagRelation.Table.NAME} 
        )
        """
    )
    fun deleteAllUnused(): Completable

    @Query(
        """
        SELECT * FROM ${Tag.Table.NAME}
        WHERE ${Tag.Column.LANGUAGE_ID} = :languageId
        """
    )
    fun getAllForLanguage(languageId: Long): Single<List<Tag>>

    @Query(
        """
        SELECT * FROM ${Tag.Table.NAME}
        WHERE ${Tag.Column.ID} = :id
        """
    )
    fun getWithId(id: Long): Single<Tag>
}
