package com.lukeneedham.vocabdrill.presentation.feature.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.ViewHolder
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.BaseAdapterFeatureDelegate
import com.lukeneedham.flowerpotrecycler.util.extensions.addDelegate
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.presentation.feature.language.create.AddLanguageDialog
import com.lukeneedham.vocabdrill.presentation.util.extension.addOnItemLongClickListener
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.decoration.LinearMarginItemDecorationCreator
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.delegate.OnItemLongClickDelegate
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()

    private val languagesAdapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<Language, LanguageItemView>().apply {
            addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addOnItemClickListener { item, _, _ -> openLanguage(item) }
            addOnItemLongClickListener {
                navigateSafe(HomeFragmentDirections.actionHomeFragmentToLanguageSettingsFragment(it.id))
            }
        }
    )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.languagesLiveData.observe(viewLifecycleOwner) {
            languagesAdapter.submitList(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        recyclerView.adapter = languagesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.VERTICAL
        }
        val decoration = LinearMarginItemDecorationCreator.fromVertical(
            resources.getDimensionPixelSize(R.dimen.language_top_item_vertical_margin),
            resources.getDimensionPixelSize(R.dimen.language_item_vertical_margin),
            resources.getDimensionPixelSize(R.dimen.language_bottom_item_vertical_margin)
        )
        recyclerView.addItemDecoration(decoration)

        addLanguageButton.setOnClickListener {
            showDialog(AddLanguageDialog())
        }
    }

    private fun openLanguage(language: Language) {
        navigateSafe(HomeFragmentDirections.actionHomeFragmentToLanguageFragment(language.id))
    }
}
