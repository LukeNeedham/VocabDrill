package com.lukeneedham.vocabdrill.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VocabGroupColourScheme(
    val mainColour: Int,
    val borderColour: Int,
    val textColour: Int
) : Parcelable
