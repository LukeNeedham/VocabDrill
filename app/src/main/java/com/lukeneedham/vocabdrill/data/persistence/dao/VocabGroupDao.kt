package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lukeneedham.vocabdrill.data.persistence.model.VocabGroup
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface VocabGroupDao {

    @Insert
    fun add(vocabGroup: VocabGroup): Completable

    @Query("SELECT * FROM ${VocabGroup.Table.NAME}")
    fun getAll(): Single<List<VocabGroup>>

    @Query("SELECT * FROM ${VocabGroup.Table.NAME}")
    fun observeAll(): Observable<List<VocabGroup>>

    @Query(
        """
        SELECT * FROM ${VocabGroup.Table.NAME}
        WHERE ${VocabGroup.Column.LANGUAGE_ID} = :languageId
        """
    )
    fun getAllForLanguage(languageId: Long): Single<List<VocabGroup>>

    @Query(
        """
        SELECT * FROM ${VocabGroup.Table.NAME}
        WHERE ${VocabGroup.Column.LANGUAGE_ID} = :languageId
        """
    )
    fun observeAllForLanguage(languageId: Long): Observable<List<VocabGroup>>

    @Query(
        """
        SELECT * FROM ${VocabGroup.Table.NAME}
        WHERE ${VocabGroup.Column.ID} = :id
        """
    )
    fun getWithId(id: Long): Single<VocabGroup>
}
