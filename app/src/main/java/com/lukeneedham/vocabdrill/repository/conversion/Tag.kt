package com.lukeneedham.vocabdrill.repository.conversion

import android.graphics.Color
import com.lukeneedham.vocabdrill.util.ColorExtraUtils
import com.lukeneedham.vocabdrill.data.persistence.db.model.Tag as TagPersistence
import com.lukeneedham.vocabdrill.domain.model.Tag as TagDomain

fun TagPersistence.toDomainModel(): TagDomain {
    val color = Color.parseColor(colorHex)
    return TagDomain(id, name, color, languageId)
}

fun TagDomain.toPersistenceModel(): TagPersistence {
    val hexColor = ColorExtraUtils.toHex(color)
    val persistence = TagPersistence(languageId, name, hexColor)
    persistence.id = id
    return persistence
}
