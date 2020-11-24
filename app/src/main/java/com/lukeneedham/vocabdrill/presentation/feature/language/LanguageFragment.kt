package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.presentation.feature.vocabgroup.create.AddVocabGroupDialog
import com.lukeneedham.vocabdrill.presentation.util.extension.addOnItemLongClickListener
import com.lukeneedham.vocabdrill.presentation.util.extension.getFlagDrawable
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.decoration.GridMarginItemDecoration
import kotlinx.android.synthetic.main.fragment_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LanguageFragment : Fragment(R.layout.fragment_language) {
    private val navArgs: LanguageFragmentArgs by navArgs()

    private val viewModel: LanguageViewModel by viewModel { parametersOf(navArgs.languageId) }

    private val adapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<VocabGroupRelations, VocabGroupItemView>().apply {
            addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addOnItemClickListener { item, _, _ -> openVocabGroup(item) }
            addOnItemLongClickListener {
                navigateSafe(
                    LanguageFragmentDirections.actionLanguageFragmentToVocabGroupSettingsFragment(
                        it.vocabGroup.id
                    )
                )
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.vocabGroupsLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.languageNameLiveData.observe(viewLifecycleOwner) {
            titleView.text = it
        }
        viewModel.countryLiveData.observe(viewLifecycleOwner) {
            settingsButton.setImageDrawable(it.getFlagDrawable(requireContext()))
        }
    }

    private fun openVocabGroup(vocabGroupRelations: VocabGroupRelations) {
        navigateSafe(
            LanguageFragmentDirections.actionLanguageFragmentToVocabGroupFragment(
                vocabGroupRelations.vocabGroup.id
            )
        )
    }

    private fun setupView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), NUM_COLUMNS).apply {
            orientation = RecyclerView.VERTICAL
        }
        // TODO: Item animation is broken currently - probably because of measuring of image view
        //  So just disable it
        recyclerView.itemAnimator = null
        val betweenMargin = resources.getDimensionPixelSize(R.dimen.vocab_group_between_item_margin)
        val decorator = GridMarginItemDecoration(
            NUM_COLUMNS,
            0,
            resources.getDimensionPixelSize(R.dimen.vocab_group_bottom_margin),
            0,
            0,
            betweenMargin,
            betweenMargin,
            betweenMargin,
            betweenMargin
        )
        recyclerView.addItemDecoration(decorator)

        backButton.setOnClickListener {
            popBackStackSafe()
        }

        settingsButton.setOnClickListener {
            navigateSafe(
                LanguageFragmentDirections.actionLanguageFragmentToLanguageSettingsFragment(
                    viewModel.languageId
                )
            )
        }

        addVocabGroupButton.setOnClickListener {
            val dialog = AddVocabGroupDialog.newInstance(viewModel.languageId)
            showDialog(dialog)
        }
    }

    companion object {
        const val NUM_COLUMNS = 2
    }
}
