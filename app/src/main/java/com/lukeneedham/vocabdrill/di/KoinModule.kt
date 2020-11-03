package com.lukeneedham.vocabdrill.di

import com.lukeneedham.vocabdrill.data.persistence.AppDatabase
import com.lukeneedham.vocabdrill.presentation.feature.home.HomeViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.create.AddLanguageViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.LanguageViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.VocabGroupItemViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.create.AddVocabGroupViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.settings.LanguageSettingsViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.settings.changeflag.ChangeLanguageFlagViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.settings.changename.ChangeLanguageNameViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.VocabEntryViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.VocabGroupViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.AddVocabEntryViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.VocabGroupSettingsViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changecolour.ChangeVocabGroupColourViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changename.ChangeVocabGroupNameViewModel
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabGroupRepository
import com.lukeneedham.vocabdrill.usecase.CalculateRelatedColours
import com.lukeneedham.vocabdrill.usecase.CheckValidLanguageName
import com.lukeneedham.vocabdrill.usecase.CheckValidVocabGroupName
import com.lukeneedham.vocabdrill.usecase.ChooseTextColourForBackground
import com.lukeneedham.vocabdrill.usecase.DeleteLanguage
import com.lukeneedham.vocabdrill.usecase.DeleteVocabGroup
import com.lukeneedham.vocabdrill.usecase.EstimateColourDistance
import com.lukeneedham.vocabdrill.usecase.ExtractFlagColoursFromCountry
import com.lukeneedham.vocabdrill.usecase.ExtractFlagColoursFromLanguageId
import com.lukeneedham.vocabdrill.usecase.FindCountriesForLanguage
import com.lukeneedham.vocabdrill.usecase.LoadFlagVectorForCountry
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabEntryRelationsForVocabGroup
import com.lukeneedham.vocabdrill.usecase.ObserveAllVocabGroupRelationsForLanguage
import com.lukeneedham.vocabdrill.usecase.ObserveLanguage
import com.lukeneedham.vocabdrill.usecase.ObserveVocabGroup
import com.lukeneedham.vocabdrill.usecase.ObserveVocabGroupRelations
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
        single { ObserveVocabGroupRelations(get(), get(), get()) }
        single { ObserveAllVocabGroupRelationsForLanguage(get(), get(), get()) }
        single { ObserveAllVocabEntryRelationsForVocabGroup(get(), get(), get()) }
        single { ObserveLanguage(get()) }
        single { DeleteLanguage(get()) }
        single { ObserveVocabGroup(get()) }
        single { DeleteVocabGroup(get()) }
        single { CheckValidLanguageName(get()) }
        single { CheckValidVocabGroupName(get()) }

        single { FindCountriesForLanguage() }

        single { ExtractFlagColoursFromLanguageId(get(), get()) }
        single { ExtractFlagColoursFromCountry(get()) }
        single { LoadFlagVectorForCountry(get()) }

        single { ChooseTextColourForBackground() }
        single { CalculateRelatedColours() }
        single { EstimateColourDistance() }
    }

    private val viewModels = module {
        viewModel { HomeViewModel(get()) }

        /* Language */
        viewModel { AddLanguageViewModel(get(), get(), get()) }
        viewModel { (languageId: Long) -> LanguageViewModel(languageId, get(), get()) }
        viewModel { (languageId: Long) -> LanguageSettingsViewModel(languageId, get(), get()) }
        viewModel { (languageId: Long) -> ChangeLanguageNameViewModel(languageId, get(), get()) }
        viewModel { (languageId: Long) -> ChangeLanguageFlagViewModel(languageId, get(), get()) }

        /* Vocab Group */
        viewModel { (languageId: Long) ->
            AddVocabGroupViewModel(languageId, get(), get(), get(), get())
        }
        viewModel { (vocabGroupId: Long) -> VocabGroupViewModel(vocabGroupId, get(), get(), get()) }
        viewModel { (vocabGroupId: Long) ->
            VocabGroupSettingsViewModel(vocabGroupId, get(), get())
        }
        viewModel { (vocabGroupId: Long) -> ChangeVocabGroupNameViewModel(vocabGroupId, get(), get()) }
        viewModel { (vocabGroupId: Long) -> ChangeVocabGroupColourViewModel(vocabGroupId, get(), get(), get()) }

        /* Vocab Entry */
        viewModel { (vocabGroupId: Long) -> AddVocabEntryViewModel(vocabGroupId, get()) }
    }

    private val vanillaViewModels = module {
        factory { VocabGroupItemViewModel(get()) }
        factory { VocabEntryViewModel(get(), get()) }
    }

    val modules =
        listOf(utils, database, daos, repositories, useCases, viewModels, vanillaViewModels)
}
