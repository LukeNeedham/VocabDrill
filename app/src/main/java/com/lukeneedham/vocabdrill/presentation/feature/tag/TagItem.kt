package com.lukeneedham.vocabdrill.presentation.feature.tag

import com.lukeneedham.vocabdrill.domain.model.Tag

sealed class TagItem {
    data class Existing(val data: Tag) : TagItem()
    class New() : TagItem()
}
