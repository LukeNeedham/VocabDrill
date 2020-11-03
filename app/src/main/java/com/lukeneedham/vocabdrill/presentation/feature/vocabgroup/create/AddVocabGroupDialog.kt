package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.create

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParamsLazy
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.flowerpotrecycler.util.extensions.scrollToCenter
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.dialog_add_vocab_group.*
import kotlinx.android.synthetic.main.dialog_add_language.closeButton
import kotlinx.android.synthetic.main.dialog_add_language.confirmButton
import kotlinx.android.synthetic.main.dialog_add_language.nameInputView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddVocabGroupDialog : BaseBottomSheetDialogFragment() {
    private val viewModel: AddVocabGroupViewModel by viewModel {
        val languageId = requireArguments().getLong(ARG_LANGUAGE)
        parametersOf(languageId)
    }

    private val coloursAdapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<Int, ColourItemView>().apply {
            addItemLayoutParamsLazy {
                val height = coloursRecycler.height
                val width = height
                RecyclerView.LayoutParams(width, height)
            }
            addOnItemClickListener { _, position, _ ->
                coloursRecycler.smoothScrollToPosition(position)
            }
        }
    )

    private val subColoursAdapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<Int, ColourItemView>().apply {
            addItemLayoutParamsLazy {
                val height = subColoursRecycler.height
                val width = height
                RecyclerView.LayoutParams(width, height)
            }
            addOnItemClickListener { _, position, _ ->
                subColoursRecycler.smoothScrollToPosition(position)
            }
        }
    )

    override val layoutResId = R.layout.dialog_add_vocab_group

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.isValidNameLiveData.observe(viewLifecycleOwner) {
            confirmButton.isEnabled = it
        }
        viewModel.flagColoursLiveData.observe(viewLifecycleOwner) {
            coloursAdapter.submitList(it)
        }
        viewModel.subColoursLiveData.observe(viewLifecycleOwner) {
            subColoursAdapter.submitList(it) {
                subColoursRecycler.post {
                    subColoursRecycler.scrollToCenter()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        coloursRecycler.adapter = coloursAdapter
        coloursRecycler.setSlideOnFling(true)
        coloursRecycler.setOrientation(DSVOrientation.HORIZONTAL)
        val coloursTransformer = ScaleTransformer.Builder()
            .setMaxScale(1f)
            .setMinScale(0.75f)
            .setPivotX(Pivot.X.CENTER)
            .setPivotY(Pivot.Y.CENTER)
            .build()
        coloursRecycler.setItemTransformer(coloursTransformer)
        coloursRecycler.addOnItemChangedListener { _, adapterPosition ->
            val colour = coloursAdapter.positionDelegate.getItemAt(adapterPosition)
            viewModel.onColourSelected(colour)
        }

        subColoursRecycler.adapter = subColoursAdapter
        subColoursRecycler.setSlideOnFling(true)
        subColoursRecycler.setOrientation(DSVOrientation.HORIZONTAL)
        val subColoursTransformer = ScaleTransformer.Builder()
            .setMaxScale(1f)
            .setMinScale(0.75f)
            .setPivotX(Pivot.X.CENTER)
            .setPivotY(Pivot.Y.CENTER)
            .build()
        subColoursRecycler.setItemTransformer(subColoursTransformer)

        nameInputView.doOnTextChanged { text: CharSequence?, _, _, _ ->
            viewModel.onNameChange(text.toString())
        }

        confirmButton.setOnClickListener {
            val selectedPosition = subColoursRecycler.currentItem
            val selectedColour = subColoursAdapter.positionDelegate.getItemAt(selectedPosition)
            viewModel.createNewVocabGroup(selectedColour)
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_LANGUAGE = "ARG_LANGUAGE"

        fun newInstance(languageId: Long) = AddVocabGroupDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_LANGUAGE, languageId)
            }
        }
    }
}
