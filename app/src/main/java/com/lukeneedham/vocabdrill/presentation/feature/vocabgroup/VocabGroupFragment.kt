package com.lukeneedham.vocabdrill.presentation.feature.vocabgroup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryRelations
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.AddVocabEntryDialog
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawable
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.LinearMarginItemDecorationCreator
import kotlinx.android.synthetic.main.fragment_vocab_group.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VocabGroupFragment : Fragment(R.layout.fragment_vocab_group) {

    private val args: VocabGroupFragmentArgs by navArgs()

    private val viewModel: VocabGroupViewModel by viewModel { parametersOf(args.vocabGroupId) }

    private val adapter =
        SingleTypeRecyclerAdapterCreator.fromRecyclerItemView<VocabEntryRelations, VocabEntryView>(
            SingleTypeAdapterConfig<VocabEntryRelations, VocabEntryView>().apply {
                addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            }
        )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.entriesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.languageNameLiveData.observe(viewLifecycleOwner) {
            languageTitleView.text = it
        }
        viewModel.vocabGroupNameLiveData.observe(viewLifecycleOwner) {
            groupTitleView.text = it
        }
        viewModel.countryLiveData.observe(viewLifecycleOwner) {
            settingsButton.setImageDrawable(it.getFlagDrawable(requireContext()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        entriesRecycler.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.VERTICAL
        }
        entriesRecycler.adapter = adapter
        val decoration = LinearMarginItemDecorationCreator.fromVertical(
            0,
            resources.getDimensionPixelSize(R.dimen.vocab_entry_vertical_margin),
            0
        )
        entriesRecycler.addItemDecoration(decoration)

        settingsButton.setOnClickListener {
            navigateSafe(
                VocabGroupFragmentDirections.actionVocabGroupFragmentToVocabGroupSettingsFragment(
                    viewModel.vocabGroupId
                )
            )
        }

        backButton.setOnClickListener {
            popBackStackSafe()
        }

        addEntryButton.setOnClickListener {
            showDialog(AddVocabEntryDialog.newInstance(viewModel.vocabGroupId))
        }
    }
}
