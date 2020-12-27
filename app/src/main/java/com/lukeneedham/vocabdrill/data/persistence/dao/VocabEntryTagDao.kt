package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lukeneedham.vocabdrill.data.persistence.model.Tag
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntryTag
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface VocabEntryTagDao {

    @Insert
    fun add(tag: VocabEntryTag): Completable

    @Query("SELECT * FROM ${VocabEntryTag.Table.NAME}")
    fun getAll(): Single<List<VocabEntryTag>>

    @Query(
        """
        SELECT * FROM ${VocabEntryTag.Table.NAME}
        WHERE ${VocabEntryTag.Column.VOCAB_ENTRY_ID} = :vocabEntryId
        """
    )
    fun getAllForVocabEntry(vocabEntryId: Long): Single<List<VocabEntryTag>>

    @Query(
        """
        SELECT * FROM ${VocabEntryTag.Table.NAME}
        WHERE ${VocabEntryTag.Column.VOCAB_ENTRY_ID} = :vocabEntryId
        """
    )
    fun observeAllForVocabEntry(vocabEntryId: Long): Observable<List<VocabEntryTag>>

    @Query(
        """
        SELECT * FROM ${VocabEntryTag.Table.NAME}
        WHERE ${VocabEntryTag.Column.ID} = :id
        """
    )
    fun getWithId(id: Long): Single<VocabEntryTag>
}
