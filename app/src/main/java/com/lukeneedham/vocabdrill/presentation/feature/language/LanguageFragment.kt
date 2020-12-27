package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.flowerpotrecycler.RecyclerAdapterCreator
import com.lukeneedham.flowerpotrecycler.adapter.config.AdapterConfig
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureConfig
import com.lukeneedham.flowerpotrecycler.adapter.itemtype.builderbinder.implementation.view.RecyclerItemViewBuilderBinder
import com.lukeneedham.flowerpotrecycler.util.ItemTypeConfigCreator
import com.lukeneedham.flowerpotrecycler.util.extensions.addItemLayoutParams
import com.lukeneedham.flowerpotrecycler.util.extensions.setLinear
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateItemView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingItemView
import com.lukeneedham.vocabdrill.presentation.util.extension.*
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.decoration.LinearMarginItemDecorationCreator
import kotlinx.android.synthetic.main.fragment_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LanguageFragment : Fragment(R.layout.fragment_language) {
    private val navArgs: LanguageFragmentArgs by navArgs()

    private val viewModel: LanguageViewModel by viewModel { parametersOf(navArgs.languageId) }

    private val adapter = RecyclerAdapterCreator.fromItemTypeConfigs<VocabEntryItem, View>(
        listOf(
            ItemTypeConfigCreator.fromRecyclerItemView<VocabEntryItem.Existing, VocabEntryExistingItemView> {
                addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
            },
            ItemTypeConfigCreator.fromBuilderBinder(
                RecyclerItemViewBuilderBinder.newInstance {
                    VocabEntryCreateItemView(requireContext()).apply {
                        callback = object : VocabEntryCreateCallback {
                            override fun onWordAChanged(
                                item: VocabEntryItem.Create,
                                newWordA: String
                            ) {
                                viewModel.onCreateItemWordAChanged(item, newWordA)
                            }

                            override fun onWordBChanged(
                                item: VocabEntryItem.Create,
                                newWordA: String
                            ) {
                                viewModel.onCreateItemWordBChanged(item, newWordA)
                            }

                            override fun save(proto: VocabEntryProto) {
                                viewModel.save(proto)
                            }
                        }
                    }
                },
                FeatureConfig<VocabEntryItem.Create, VocabEntryCreateItemView>().apply {
                    addItemLayoutParams(RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                }
            )
        ),
        AdapterConfig<VocabEntryItem, View>().apply {
            setLinear(object : DiffUtil.ItemCallback<VocabEntryItem>() {
                override fun areItemsTheSame(
                    oldItem: VocabEntryItem,
                    newItem: VocabEntryItem
                ): Boolean {
                    return when (oldItem) {
                        is VocabEntryItem.Create -> newItem is VocabEntryItem.Create
                        is VocabEntryItem.Existing ->
                            newItem is VocabEntryItem.Existing &&
                                    oldItem.data.vocabEntry.id == newItem.data.vocabEntry.id
                    }
                }

                override fun areContentsTheSame(
                    oldItem: VocabEntryItem,
                    newItem: VocabEntryItem
                ): Boolean {
                    return when (oldItem) {
                        is VocabEntryItem.Create -> newItem is VocabEntryItem.Create
                        is VocabEntryItem.Existing -> oldItem == newItem
                    }
                }
            })
        }
    )

    /**
     * Has value true when the create item is awaiting focus.
     * It will be given focus as soon as it comes on screen,
     * and then this flag is considered resolved
     */
    private var focusCreateItemQueued: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.vocabEntriesLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.languageNameLiveData.observe(viewLifecycleOwner) {
            titleView.text = it
        }
        viewModel.countryLiveData.observe(viewLifecycleOwner) {
            settingsButton.setImageDrawable(it.getFlagDrawable(requireContext()))
        }
    }

    private fun setupView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.VERTICAL
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val scrollPercent = recyclerView.computeVerticalScrollPercent()
                if(focusCreateItemQueued) {
                    if (scrollPercent >= 0.99f) {
                        focusCreateItemQueued = false
                        val createView = getCreateItemView()
                        createView?.requestFocus()
                    }
                }
                val minScrollPercent = 0.8f
                val addButtonScale = if (scrollPercent < minScrollPercent) {
                    1f
                } else {
                    val factor = 1 / (1 - minScrollPercent)
                    val normalisedScroll = (scrollPercent - minScrollPercent) * factor
                    1 - normalisedScroll
                }
                addButton.setScale(addButtonScale)
            }
        })

        val betweenMargin = resources.getDimensionPixelSize(R.dimen.vocab_entry_between_item_margin)
        val bottomMargin = resources.getDimensionPixelSize(R.dimen.vocab_entry_bottom_item_margin)
        val decorator =
            LinearMarginItemDecorationCreator.fromVertical(0, betweenMargin, bottomMargin)
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

        addButton.setOnClickListener {
            focusCreateItemQueued = true
            // Last item is always the new one
            scrollToEnd()
        }
    }

    private fun getCreateItemView(): VocabEntryCreateItemView? {
        val lastItemPosition = adapter.itemCount - 1
        val view = recyclerView.findViewHolderForAdapterPosition(lastItemPosition)?.itemView
        return view as? VocabEntryCreateItemView
    }

    private fun scrollToEnd() {
        val lastItemPosition = adapter.itemCount - 1
        recyclerView.smoothScrollToPosition(lastItemPosition)
    }
}
