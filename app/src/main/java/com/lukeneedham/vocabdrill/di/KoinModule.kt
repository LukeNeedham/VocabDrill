package com.lukeneedham.vocabdrill.di

import com.lukeneedham.vocabdrill.data.persistence.db.AppDatabase
import com.lukeneedham.vocabdrill.data.persistence.preferences.PreferencesDao
import com.lukeneedham.vocabdrill.domain.model.LearnSet
import com.lukeneedham.vocabdrill.presentation.feature.home.HomeViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.LanguageViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.create.AddLanguageViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.entries.VocabEntriesViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.settings.LanguageSettingsViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.settings.changeflag.ChangeLanguageFlagViewModel
import com.lukeneedham.vocabdrill.presentation.feature.language.settings.changename.ChangeLanguageNameViewModel
import com.lukeneedham.vocabdrill.presentation.feature.learn.LearnViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.CreateEntryItemViewModel
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.ExistingEntryItemViewModel
import com.lukeneedham.vocabdrill.repository.LanguageRepository
import com.lukeneedham.vocabdrill.repository.TagRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryRepository
import com.lukeneedham.vocabdrill.repository.VocabEntryTagRelationRepository
import com.lukeneedham.vocabdrill.usecase.*
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
        single { get<AppDatabase>().tagDao() }
        single { get<AppDatabase>().vocabEntryTagDao() }

        single { PreferencesDao(androidContext()) }
    }

    private val repositories = module {
        single { LanguageRepository(get()) }
        single { VocabEntryRepository(get()) }
        single { TagRepository(get(), get()) }
        single { VocabEntryTagRelationRepository(get(), get()) }
    }

    private val useCases = module {
        /* Language */
        single { ObserveLanguage(get()) }
        single { DeleteLanguage(get()) }
        single { CheckValidLanguageName(get()) }
        single { ObserveAllVocabEntryAndTagsForLanguage(get(), get(), get()) }

        /* Entries */
        single { DeleteVocabEntry(get()) }
        single { UpdateVocabEntry(get()) }
        single { AddVocabEntry(get(), get()) }
        single { ObserveVocabEntryForId(get()) }

        /* Countries */
        single { ExtractFlagColoursFromLanguageId(get(), get()) }
        single { ExtractFlagColoursFromCountry(get()) }
        single { LoadFlagVectorForCountry(get()) }
        single { FindCountriesForLanguage() }

        /* Colour */
        single { ChooseTextColourForBackground() }
        single { CalculateRelatedColours() }
        single { EstimateColourDistance() }

        /* Tag */
        single { AddTagToVocabEntry(get()) }
        single { AddNewTag(get()) }
        single { CalculateColorForNewTag(get(), get()) }
        single { FindTagNameMatches(get()) }
        single { DeleteUnusedTags(get()) }
        single { DeleteTagFromVocabEntry(get()) }

        /* Learn */
        single { PlaySoundEffect(androidContext()) }
    }

    private val viewModels = module {
        viewModel { HomeViewModel(get()) }

        /* Language */
        viewModel { AddLanguageViewModel(get(), get(), get()) }
        viewModel { (languageId: Long) ->
            LanguageViewModel(
                languageId,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
        viewModel { (languageId: Long) -> LanguageSettingsViewModel(languageId, get(), get()) }
        viewModel { (languageId: Long) -> ChangeLanguageNameViewModel(languageId, get(), get()) }
        viewModel { (languageId: Long) -> ChangeLanguageFlagViewModel(languageId, get(), get()) }

        /* Vocab Entry */
        factory { (languageId: Long) -> VocabEntriesViewModel(languageId, get()) }
        factory { (entryId: Long) -> ExistingEntryItemViewModel(entryId, get()) }
        factory { CreateEntryItemViewModel() }

        /* Learn */
        viewModel { (learnSet: LearnSet) -> LearnViewModel(learnSet, get()) }
    }

    val modules = listOf(utils, database, daos, repositories, useCases, viewModels)
}
