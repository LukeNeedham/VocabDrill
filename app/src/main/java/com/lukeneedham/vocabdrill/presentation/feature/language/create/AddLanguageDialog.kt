package com.lukeneedham.vocabdrill.presentation.feature.language.create

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParamsLazy
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.CountryMatches
import com.lukeneedham.vocabdrill.domain.model.Flag
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawable
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.dialog_add_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddLanguageDialog : BaseBottomSheetDialogFragment() {
    private val viewModel: AddLanguageViewModel by viewModel()

    private val flagsAdapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<Flag, FlagItemView>().apply {
            addItemLayoutParamsLazy {
                val height = flagsRecycler.height
                val width = height
                RecyclerView.LayoutParams(width, height)
            }
            addOnItemClickListener { _, position, _ ->
                flagsRecycler.smoothScrollToPosition(position)
            }
        }
    )

    override val layoutResId = R.layout.dialog_add_language

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.countriesLiveData.observe(viewLifecycleOwner) {
            val exhaustive = when (it) {
                is CountryMatches.Searching -> {
                    flagsRecycler.visibility = View.INVISIBLE
                    loadingFlagsIcon.visibility = View.VISIBLE
                    flagsAdapter.submitList(emptyList())
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
        viewModel.isConfirmEnabledLiveData.observe(viewLifecycleOwner) {
            confirmButton.isEnabled = it
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

        nameInputView.doOnTextChanged { text: CharSequence?, _, _, _ ->
            viewModel.onNameChange(text.toString())
        }

        confirmButton.setOnClickListener {
            val selectedPosition = flagsRecycler.currentItem
            val selected = flagsAdapter.positionDelegate.getItemAt(selectedPosition)
            val selectedCountry = selected.country
            viewModel.createNewLanguage(selectedCountry)
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }
}
