package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

sealed class TagItemProps {
    data class Existing(val data: Tag, val textColor: Int) : TagItemProps()
    data class Create(val text: String, val selection: TextSelection?) : TagItemProps()
}

fun TagItemProps.isSameItem(other: TagItemProps): Boolean {
    return when (this) {
        is TagItemProps.Create -> other is TagItemProps.Create
        is TagItemProps.Existing -> other is TagItemProps.Existing && this.data.id == other.data.id
    }
}
