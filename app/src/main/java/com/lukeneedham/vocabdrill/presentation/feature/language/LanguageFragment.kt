package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabGroupProto
import com.lukeneedham.vocabdrill.domain.model.VocabGroupRelations
import com.lukeneedham.vocabdrill.presentation.feature.language.addgroup.AddGroupCallback
import com.lukeneedham.vocabdrill.presentation.feature.language.addgroup.AddGroupDialog
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.popBackStackSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.GridMarginItemDecoration
import kotlinx.android.synthetic.main.fragment_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LanguageFragment : Fragment(R.layout.fragment_language), AddGroupCallback {
    private val navArgs: LanguageFragmentArgs by navArgs()

    private val viewModel: LanguageViewModel by viewModel { parametersOf(navArgs.languageId) }

    private val adapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<VocabGroupRelations, VocabGroupItemView>().apply {
            addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addOnItemClickListener { item, _, _ -> openVocabGroup(item) }
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
    }

    override fun addGroup(groupProto: VocabGroupProto) {
        viewModel.addGroup(groupProto)
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
        val decorator = GridMarginItemDecoration(
            NUM_COLUMNS,
            resources.getDimensionPixelSize(R.dimen.vocab_group_item_margin),
            false
        )
        recyclerView.addItemDecoration(decorator)

        backButton.setOnClickListener {
            popBackStackSafe()
        }

        addVocabGroupButton.setOnClickListener {
            val dialog = AddGroupDialog.newInstance(viewModel.languageId)
            showDialog(dialog)
        }
    }

    companion object {
        const val NUM_COLUMNS = 2
    }
}
