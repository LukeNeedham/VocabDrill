package com.lukeneedham.vocabdrill.domain

import com.lukeneedham.vocabdrill.domain.model.Country
import com.lukeneedham.vocabdrill.util.UnknownCountryCode

object UnknownCountry {
    val country = Country(UnknownCountryCode.code)
}
