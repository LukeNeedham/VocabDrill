package com.lukeneedham.vocabdrill.presentation.util.composable

import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/** This is a Koin util because the official Compose support for Koin is broken right now */
inline fun <reified T> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T = GlobalContext.get().get(qualifier, parameters)
