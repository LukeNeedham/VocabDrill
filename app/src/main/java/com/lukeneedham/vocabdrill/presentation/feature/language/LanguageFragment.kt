package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.InteractionSection
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItemPresentationData
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingCallback
import com.lukeneedham.vocabdrill.presentation.util.TextSelection
import com.lukeneedham.vocabdrill.presentation.util.extension.*
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.decoration.LinearMarginItemDecorationCreator
import kotlinx.android.synthetic.main.fragment_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LanguageFragment : Fragment(R.layout.fragment_language) {
    private val navArgs: LanguageFragmentArgs by navArgs()

    private val viewModel: LanguageViewModel by viewModel { parametersOf(navArgs.languageId) }

    private val layoutManager by lazy {
        LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.VERTICAL
        }
    }

    private val vocabEntryExistingCallback: VocabEntryExistingCallback =
        object : VocabEntryExistingCallback {
            override fun onWordAChanged(
                itemPresentationData: VocabEntryItemPresentationData.Existing,
                newWordA: String,
                selection: TextSelection
            ) {
                viewModel.onExistingItemWordAChanged(
                    itemPresentationData.data.entryId,
                    newWordA,
                    selection
                )
            }

            override fun onWordBChanged(
                itemPresentationData: VocabEntryItemPresentationData.Existing,
                newWordB: String,
                selection: TextSelection
            ) {
                viewModel.onExistingItemWordBChanged(
                    itemPresentationData.data.entryId,
                    newWordB,
                    selection
                )
            }

            override fun onViewModeChanged(
                itemPresentationData: VocabEntryItemPresentationData.Existing,
                viewMode: ViewMode
            ) {
                viewModel.onViewModeChanged(itemPresentationData, viewMode)
            }

            override fun onDelete(itemPresentationData: VocabEntryItemPresentationData.Existing) {
                viewModel.deleteEntry(itemPresentationData.data.entryId)
            }
        }

    private val vocabEntryCreateCallback = object : VocabEntryCreateCallback {
        override fun onWordAChanged(newWordA: String, selection: TextSelection) {
            viewModel.onCreateItemWordAChanged(newWordA, selection)
        }

        override fun onWordBChanged(newWordA: String, selection: TextSelection) {
            viewModel.onCreateItemWordBChanged(newWordA, selection)
        }

        override fun onInteraction(section: InteractionSection, selection: TextSelection) {
            viewModel.onCreateItemInteraction(section, selection)
        }

        override fun save(proto: VocabEntryProto) {
            viewModel.save(proto)
        }
    }

    private val tagCreateCallback = object : TagCreateCallback {
        override fun onNameChanged(tagItem: TagItem.Create, name: String) {
            if (name.isBlank()) {
                tagSuggestionsView.visibility = View.GONE
                return
            }
            val entryItem = tagItem.vocabEntryItem
            viewModel.requestTagMatches(entryItem, name)
            tagSuggestionsView.onSuggestionClickListener = { tag ->
                viewModel.addTagToVocabEntry(entryItem, tag)
                tagSuggestionsView.visibility = View.GONE
            }
            tagSuggestionsView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                // TODO: Position under tag view, which needs to be a parameter
                topMargin = 100
                leftMargin = 100
            }
            tagSuggestionsView.visibility = View.VISIBLE
        }

        override fun onStartNameInput() {
            // TODO: Fix or delete
            //viewModel.onCreateItemInteraction(InteractionSection.Other, null)
        }
    }

    private val vocabEntriesAdapter =
        VocabEntriesAdapter(vocabEntryExistingCallback, vocabEntryCreateCallback, tagCreateCallback)

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
            vocabEntriesAdapter.submitList(it)
        }
        viewModel.languageNameLiveData.observe(viewLifecycleOwner) {
            titleView.text = it
        }
        viewModel.countryLiveData.observe(viewLifecycleOwner) {
            settingsButton.setImageDrawable(it.getFlagDrawable(requireContext()))
        }
        viewModel.tagSuggestionsLiveData.observe(viewLifecycleOwner) {
            tagSuggestionsView?.setTags(it)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveDataChanges()
    }

    private fun setupView() {
        recyclerView.adapter = vocabEntriesAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val scrollPercent = recyclerView.computeVerticalScrollPercent()
                if (focusCreateItemQueued) {
                    if (scrollPercent >= MIN_SCROLL_PERCENT_TO_TRIGGER_CREATE_FOCUS) {
                        focusCreateItemQueued = false
                        viewModel.focusCreateItem()
                    }
                }
                val addButtonScale = if (scrollPercent < MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON) {
                    1f
                } else {
                    val factor = 1 / (1 - MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON)
                    val normalisedScroll =
                        (scrollPercent - MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON) * factor
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
            val lastItemPosition = getLastItemPosition()
            val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

            if (lastVisiblePosition == lastItemPosition) {
                viewModel.focusCreateItem()
                showKeyboard()
            } else {
                focusCreateItemQueued = true
                recyclerView.smoothScrollToPosition(lastItemPosition)
                recyclerView.doOnNextLayout {
                    showKeyboard()
                }
            }
        }
    }

    private fun getLastItemPosition() = vocabEntriesAdapter.itemCount - 1

    companion object {
        const val MIN_SCROLL_PERCENT_TO_TRIGGER_CREATE_FOCUS = 0.99f
        const val MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON = 0.8f
    }
}
