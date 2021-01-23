package com.lukeneedham.vocabdrill.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VocabEntryAndTags(
    val entry: VocabEntry,
    val tags: List<Tag>
) : Parcelable
