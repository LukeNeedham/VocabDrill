package com.lukeneedham.vocabdrill.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TagProto(val name: String, val colour: Int, val languageId: Long) : Parcelable
