package com.lukeneedham.vocabdrill.presentation.feature.language.settings.changeflag

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParamsLazy
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.CountryMatches
import com.lukeneedham.vocabdrill.domain.model.Flag
import com.lukeneedham.vocabdrill.presentation.feature.language.create.FlagItemView
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawable
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.dialog_change_language_flag.*
import kotlinx.android.synthetic.main.dialog_change_language_flag.flagsRecycler
import kotlinx.android.synthetic.main.dialog_change_language_name.closeButton
import kotlinx.android.synthetic.main.dialog_change_language_name.confirmButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeLanguageFlagDialog : BaseBottomSheetDialogFragment() {
    private val viewModel: ChangeLanguageFlagViewModel by viewModel {
        val languageId = requireArguments().getLong(ARG_LANGUAGE)
        parametersOf(languageId)
    }

    private val flagsAdapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<Flag, FlagItemView>().apply {
            addItemLayoutParamsLazy {
                val height = flagsRecycler.height
                val width = height
                RecyclerView.LayoutParams(width, height)
            }
        }
    )

    override val layoutResId = R.layout.dialog_change_language_flag

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.countriesLiveData.observe(viewLifecycleOwner) {
            val exhaustive = when(it) {
                is CountryMatches.Searching -> {
                    flagsRecycler.visibility = View.INVISIBLE
                    loadingFlagsIcon.visibility = View.VISIBLE
                }
                is CountryMatches.Found -> {
                    val flags = it.countries.mapNotNull {
                        val flag = it.getFlagDrawable(requireContext()) ?: return@mapNotNull null
                        Flag(flag, it)
                    }
                    flagsAdapter.submitList(flags) {
                        flagsRecycler.post {
                            flagsRecycler.visibility = View.VISIBLE
                            loadingFlagsIcon.visibility = View.GONE
                            flagsRecycler.scrollToPosition(flagsAdapter.itemCount)
                        }
                    }
                }
            }
        }
        viewModel.languageLiveData.observe(viewLifecycleOwner) { language ->
            val country = language.country
            val flagPosition =
                flagsAdapter.positionDelegate.getItems().indexOfFirst { it.country == country }
            if (flagPosition != -1) {
                flagsRecycler.scrollToPosition(flagPosition)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        flagsRecycler.adapter = flagsAdapter
        flagsRecycler.setSlideOnFling(true)
        flagsRecycler.setOrientation(DSVOrientation.HORIZONTAL)
        val transformer = ScaleTransformer.Builder()
            .setMaxScale(1f)
            .setMinScale(0.75f)
            .setPivotX(Pivot.X.CENTER)
            .setPivotY(Pivot.Y.CENTER)
            .build()
        flagsRecycler.setItemTransformer(transformer)

        confirmButton.setOnClickListener {
            val selectedPosition = flagsRecycler.currentItem
            val selected = flagsAdapter.positionDelegate.getItemAt(selectedPosition)
            val selectedCountry = selected.country
            viewModel.updateLanguage(selectedCountry)
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_LANGUAGE = "ARG_LANGUAGE"

        fun newInstance(languageId: Long) = ChangeLanguageFlagDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_LANGUAGE, languageId)
            }
        }
    }
}
