package com.lukeneedham.vocabdrill.di

import com.lukeneedham.vocabdrill.data.persistence.AppDatabase
import com.lukeneedham.vocabdrill.presentation.feature.home.HomeViewModel
import com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage.AddLanguageViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.LanguageViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.VocabGroupItemViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.addgroup.AddGroupViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.VocabGroupViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.addentry.AddEntryViewModel
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.CalculateRelatedColours
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground
import com.lukeneedham.vocabdrill.usecase.ExtractFlagColours
import com.lukeneedham.vocabdrill.usecase.FindCountriesForLanguage
import com.lukeneedham.vocabdrill.usecase.LoadFlagVectorForCountry
import com.lukeneedham.vocabdrill.usecase.LoadVocabGroupRelations
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabGroupRelationsForLanguage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModule {

    private val utils = module {
        single {
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
    }

    private val database = module {
        single { AppDatabase.newInstance(androidContext()) }
    }

    private val daos = module {
        single { get<AppDatabase>().userDao() }
        single { get<AppDatabase>().vocabEntryDao() }
        single { get<AppDatabase>().vocabGroupDao() }
    }

    private val repositories = module {
        single { LanguageRepository(get()) }
        single { VocabGroupRepository(get()) }
        single { VocabEntryRepository(get()) }
    }

    private val useCases = module {
        single { LoadVocabGroupRelations(get(), get(), get()) }
        single { ObserveAllVocabGroupRelationsForLanguage(get(), get(), get()) }
        single { FindCountriesForLanguage() }
        single { ExtractFlagColours(get()) }
        single { LoadFlagVectorForCountry(get()) }
        single { ChooseTextColourForBackground() }
        single { CalculateRelatedColours() }
    }

    private val viewModels = module {
        viewModel { HomeViewModel(get()) }
        viewModel { AddLanguageViewModel(get(), get()) }
        viewModel { (languageId: Long) -> LanguageViewModel(languageId, get(), get(), get()) }
        viewModel { (languageId: Long) ->
            AddGroupViewModel(languageId, get(), get(), get(), get())
        }
        viewModel { (vocabGroupId: Long) -> VocabGroupViewModel(vocabGroupId, get(), get()) }
        viewModel { (vocabGroupId: Long) -> AddEntryViewModel(vocabGroupId) }
    }

    private val vanillaViewModels = module {
        factory { VocabGroupItemViewModel(get()) }
    }

    val modules =
        listOf(utils, database, daos, repositories, useCases, viewModels, vanillaViewModels)
}
