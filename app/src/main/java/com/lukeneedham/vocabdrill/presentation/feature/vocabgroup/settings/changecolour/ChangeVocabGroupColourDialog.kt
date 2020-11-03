package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.settings.changecolour

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParamsLazy
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.flowerpotrecycler.util.extensions.scrollToCenter
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.create.ColourItemView
import com.lukeneedham.vocabdrill.presentation.util.BaseBottomSheetDialogFragment
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.dialog_change_vocab_group_colour.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeVocabGroupColourDialog : BaseBottomSheetDialogFragment() {
    private val viewModel: ChangeVocabGroupColourViewModel by viewModel {
        val vocabGroupId = requireArguments().getLong(ARG_VOCAB_GROUP_ID)
        parametersOf(vocabGroupId)
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

    override val layoutResId = R.layout.dialog_change_vocab_group_colour

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.vocabGroupColorsLiveData.observe(viewLifecycleOwner) {
            coloursAdapter.submitList(it.colors) {
                val selected = it.selectedColour
                if (selected != null) {
                    val position = coloursAdapter.positionDelegate.getPositionOfItem(selected)
                    if (position != RecyclerView.NO_POSITION) {
                        coloursRecycler.post {
                            coloursRecycler.scrollToPosition(position)
                        }
                    }
                }
            }
        }
        viewModel.subColoursLiveData.observe(viewLifecycleOwner) {
            subColoursAdapter.submitList(it.colors) {
                val selected = it.selectedColour
                if (selected != null) {
                    val position = subColoursAdapter.positionDelegate.getPositionOfItem(selected)
                    subColoursRecycler.post {
                        if (position != RecyclerView.NO_POSITION) {
                            subColoursRecycler.scrollToPosition(position)
                        } else {
                            subColoursRecycler.scrollToCenter()
                        }
                    }
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

        confirmButton.setOnClickListener {
            val selectedPosition = subColoursRecycler.currentItem
            val selectedColour = subColoursAdapter.positionDelegate.getItemAt(selectedPosition)
            viewModel.updateVocabGroup(selectedColour)
            dismiss()
        }

        closeButton.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        private const val ARG_VOCAB_GROUP_ID = "ARG_VOCAB_GROUP_ID"

        fun newInstance(vocabGroupId: Long) = ChangeVocabGroupColourDialog().apply {
            arguments = Bundle().apply {
                putLong(ARG_VOCAB_GROUP_ID, vocabGroupId)
            }
        }
    }
}
