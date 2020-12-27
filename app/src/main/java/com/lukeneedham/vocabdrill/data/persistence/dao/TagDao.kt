package com.lukeneedham.vocabdrill.data.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.lukeneedham.vocabdrill.data.persistence.model.Tag
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TagDao {

    @Insert
    fun add(tag: Tag): Completable

    @Query("SELECT * FROM ${Tag.Table.NAME}")
    fun getAll(): Single<List<Tag>>

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
