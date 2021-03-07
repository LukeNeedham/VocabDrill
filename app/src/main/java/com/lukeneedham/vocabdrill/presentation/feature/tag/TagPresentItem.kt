package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag
import com.lukeneedham.vocabdrill.presentation.util.TextSelection

sealed class TagPresentItem {
    data class Existing(val data: Tag, val textColor: Int) : TagPresentItem()
    data class Create(val text: String, val selection: TextSelection?) : TagPresentItem()
}

fun TagPresentItem.isSameItem(other: TagPresentItem): Boolean {
    return when (this) {
        is TagPresentItem.Create -> other is TagPresentItem.Create
        is TagPresentItem.Existing -> other is TagPresentItem.Existing && this.data.id == other.data.id
    }
}
