package com.lukeneedham.vocabdrill.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LearnBook(
    val entries: List<VocabEntry>,
    val colourScheme: TagColourScheme
) : Parcelable
