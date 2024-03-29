package com.lukeneedham.vocabdrill.data.persistence.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lukeneedham.vocabdrill.data.persistence.db.model.Language
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface LanguageDao {

    @Insert
    fun add(language: Language): Completable

    @Update
    fun update(language: Language): Completable

    @Query("DELETE FROM ${Language.Table.NAME} WHERE ${Language.Column.ID} = :id")
    fun deleteWithId(id: Long): Completable

    @Query("SELECT * FROM ${Language.Table.NAME}")
    fun getAll(): Single<List<Language>>

    @Query("SELECT * FROM ${Language.Table.NAME}")
    fun observeAll(): Observable<List<Language>>

    @Query(
        """
        SELECT * FROM ${Language.Table.NAME}
        WHERE ${Language.Column.ID} = :id
        """
    )
    fun getWithId(id: Long): Single<Language>

    @Query(
        """
        SELECT * FROM ${Language.Table.NAME}
        WHERE ${Language.Column.ID} = :id
        """
    )
    fun observeWithId(id: Long): Observable<Language>
}
