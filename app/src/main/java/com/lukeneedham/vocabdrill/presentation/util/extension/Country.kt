package com.lukeneedham.vocabdrill.presentation.util.extension

import android.content.Context
import android.graphics.drawable.Drawable
import com.lukeneedham.circleflagsandroid.FlagProvider
import com.lukeneedham.vocabdrill.domain.model.Country

fun Country.getFlagDrawable(context: Context): Drawable? {
    return FlagProvider.getFlagFromCountryAlpha2Code(alpha2Code, context)
}

fun Country.getFlagDrawableId(context: Context): Int {
    val id = FlagProvider.getFlagResIdFromCountryAlpha2Code(alpha2Code, context)
    return if (id == 0) {
        FlagProvider.getUnknownFlagResId()
    } else {
        id
    }
}
