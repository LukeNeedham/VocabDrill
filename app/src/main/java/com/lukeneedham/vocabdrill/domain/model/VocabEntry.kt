package com.lukeneedham.vocabdrill.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/** The Vocab Entry, without tags */
@Parcelize
data class VocabEntry(
    val id: Long,
    val wordA: String,
    val wordB: String,
    val languageId: Long
) : Parcelable
