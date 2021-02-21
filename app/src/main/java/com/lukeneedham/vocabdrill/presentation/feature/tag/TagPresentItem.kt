package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag

sealed class TagPresentItem {
    data class Existing(val data: Tag, val textColor: Int) : TagPresentItem()
    object Create : TagPresentItem()
}
