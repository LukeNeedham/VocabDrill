package com.lukeneedham.vocabdrill.presentation.feature.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.SingleTypeRecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.SingleTypeAdapterConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.addOnItemClickListener
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.Language
import com.lukeneedham.vocabdrill.domain.model.LanguageProto
import com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage.AddLanguageCallback
import com.lukeneedham.vocabdrill.presentation.feature.home.addlanguage.AddLanguageDialog
import com.lukeneedham.vocabdrill.presentation.util.extension.navigateSafe
import com.lukeneedham.vocabdrill.presentation.util.extension.showDialog
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.LinearMarginItemDecorationCreator
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home), AddLanguageCallback {

    private val viewModel: HomeViewModel by viewModel()

    private val languagesAdapter = SingleTypeRecyclerAdapterCreator.fromRecyclerItemView(
        SingleTypeAdapterConfig<Language, LanguageItemView>().apply {
            addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            addOnItemClickListener { item, _, _ -> openLanguage(item) }
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

    override fun addLanguage(languageProto: LanguageProto) {
        viewModel.addLanguage(languageProto)
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
