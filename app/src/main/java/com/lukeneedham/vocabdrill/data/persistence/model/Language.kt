package com.lukeneedham.vocabdrill.data.persistence.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Language.Table.NAME)
data class Language(
    @ColumnInfo(name = Column.NAME) val name: String,
    /** The country code (alpha-2) of the country chosen to supply the flag for this language */
    @ColumnInfo(name = Column.COUNTRY_CODE) val flagCountryAlpha2Code: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Column.ID)
    var id: Long = 0

    object Table {
        const val NAME = "languages"
    }

    object Column {
        const val ID = "id"
        const val NAME = "name"
        const val COUNTRY_CODE = "country_code"
    }
}
