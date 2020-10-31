package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface VocabEntryDao {

    @Insert
    fun add(vocabGroup: VocabEntry): Completable

    @Query("SELECT * FROM ${VocabEntry.Table.NAME}")
    fun getAll(): Single<List<VocabEntry>>

    @Query(
        """
        SELECT * FROM ${VocabEntry.Table.NAME}
        WHERE ${VocabEntry.Column.VOCAB_GROUP_ID} = :vocabGroupId
        """
    )
    fun getAllForVocabGroup(vocabGroupId: Long): Single<List<VocabEntry>>

    @Query(
        """
        SELECT * FROM ${VocabEntry.Table.NAME}
        WHERE ${VocabEntry.Column.VOCAB_GROUP_ID} = :vocabGroupId
        """
    )
    fun observeAllForVocabGroup(vocabGroupId: Long): Observable<List<VocabEntry>>
}
