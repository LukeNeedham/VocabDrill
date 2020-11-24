package com.lukeneedham.vocabdrill.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TagColourScheme(
    val mainColour: Int,
    val borderColour: Int,
    val textColour: Int
) : Parcelable
