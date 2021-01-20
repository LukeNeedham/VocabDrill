package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntryTagRelation
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface VocabEntryTagRelationDao {

    @Insert
    fun add(tagRelation: VocabEntryTagRelation): Single<Long>

    @Query("""
        DELETE FROM ${VocabEntryTagRelation.Table.NAME}
        WHERE ${VocabEntryTagRelation.Column.TAG_ID} = :tagId
        AND ${VocabEntryTagRelation.Column.VOCAB_ENTRY_ID} = :vocabEntryId
    """)
    fun delete(vocabEntryId: Long, tagId: Long): Completable

    @Query("SELECT * FROM ${VocabEntryTagRelation.Table.NAME}")
    fun getAll(): Single<List<VocabEntryTagRelation>>

    @Query(
        """
        SELECT * FROM ${VocabEntryTagRelation.Table.NAME}
        WHERE ${VocabEntryTagRelation.Column.VOCAB_ENTRY_ID} = :vocabEntryId
        """
    )
    fun getAllForVocabEntry(vocabEntryId: Long): Single<List<VocabEntryTagRelation>>

    @Query(
        """
        SELECT * FROM ${VocabEntryTagRelation.Table.NAME}
        WHERE ${VocabEntryTagRelation.Column.VOCAB_ENTRY_ID} = :vocabEntryId
        """
    )
    fun observeAllForVocabEntry(vocabEntryId: Long): Observable<List<VocabEntryTagRelation>>

    @Query(
        """
        SELECT * FROM ${VocabEntryTagRelation.Table.NAME}
        WHERE ${VocabEntryTagRelation.Column.ID} = :id
        """
    )
    fun getWithId(id: Long): Single<VocabEntryTagRelation>
}
