package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lukeneedham.vocabdrill.data.persistence.model.VocabEntry
import com.lukeneedham.vocabdrill.data.persistence.model.VocabGroup
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface VocabEntryDao {

    @Insert
    fun add(vocabEntry: VocabEntry): Completable

    @Update
    fun update(vocabEntry: VocabEntry): Completable

    @Query("DELETE FROM ${VocabEntry.Table.NAME} WHERE ${VocabEntry.Column.ID} = :id")
    fun deleteWithId(id: Long): Completable

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

    @Query(
        """
        SELECT * FROM ${VocabEntry.Table.NAME}
        WHERE ${VocabEntry.Column.ID} = :id
        """
    )
    fun observeWithId(id: Long): Observable<VocabEntry>

    @Query(
        """
        SELECT * FROM ${VocabEntry.Table.NAME}
        WHERE ${VocabEntry.Column.ID} = :id
        """
    )
    fun getWithId(id: Long): Single<VocabEntry>
}
