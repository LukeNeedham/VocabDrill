package com.lukeneedham.vocabdrill.usecase

import android.content.Context
import com.lukeneedham.circleflagsandroid.FlagProvider
import com.lukeneedham.vocabdrill.domain.model.Country
import com.sdsmdg.harjot.vectormaster.VectorMasterDrawable

class LoadFlagVectorForCountry(
    private val context: Context
) {
    operator fun invoke(country: Country): VectorMasterDrawable {
        val resId = FlagProvider.getFlagResIdFromCountryAlpha2Code(country.alpha2Code, context)
        return VectorMasterDrawable(context, resId)
    }
}
