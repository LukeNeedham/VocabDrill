package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItemProps
import com.lukeneedham.vocabdrill.presentation.feature.tag.suggestion.TagSuggestion
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.InteractionSection
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
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

    private lateinit var layoutManager: LinearLayoutManager

    private val vocabEntryExistingCallback: VocabEntryExistingCallback =
        object : VocabEntryExistingCallback {
            override fun onWordAChanged(
                editItem: VocabEntryEditItem.Existing,
                newWordA: String,
                selection: TextSelection
            ) {
                viewModel.onExistingItemWordAChanged(
                    editItem.entry.id,
                    newWordA,
                    selection
                )
            }

            override fun onWordBChanged(
                editItem: VocabEntryEditItem.Existing,
                newWordB: String,
                selection: TextSelection
            ) {
                viewModel.onExistingItemWordBChanged(
                    editItem.entry.id,
                    newWordB,
                    selection
                )
            }

            override fun onViewModeChanged(
                editItem: VocabEntryEditItem.Existing,
                viewMode: ViewMode
            ) {
                viewModel.onViewModeChanged(editItem, viewMode)
            }

            override fun onDelete(editItem: VocabEntryEditItem.Existing) {
                viewModel.deleteEntry(editItem.entry.id)
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
            viewModel.addEntry(proto)
        }
    }

    private val tagCreateCallback = newTagCreateCallback()

    private val vocabEntriesAdapter = VocabEntriesAdapter(
        vocabEntryExistingCallback,
        vocabEntryCreateCallback,
        tagCreateCallback,
        ::onTagExistingClick,
        ::onTagSuggestionClick
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
        viewModel.vocabEntriesUpdateLiveData.observe(viewLifecycleOwner) {
            vocabEntriesAdapter.submitList(it.items, it.refresh)
        }
        viewModel.languageNameLiveData.observe(viewLifecycleOwner) {
            titleView.text = it
        }
        viewModel.countryLiveData.observe(viewLifecycleOwner) {
            languageIcon.setImageDrawable(it.getFlagDrawable(requireContext()))
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveChanges()
    }

    private fun setupView() {
        recyclerView.adapter = vocabEntriesAdapter
        layoutManager = LinearLayoutManager(requireContext()).apply {
            orientation = RecyclerView.VERTICAL
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val scrollPercent = recyclerView.computeVerticalScrollPercent()

                // Focus pending
                if (focusCreateItemQueued) {
                    if (scrollPercent >= MIN_SCROLL_PERCENT_TO_TRIGGER_CREATE_FOCUS) {
                        focusCreateItemQueued = false
                        viewModel.focusCreateItem()
                    }
                }

                // Button scale
                val fabScale = if (scrollPercent < MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON) {
                    1f
                } else {
                    val factor = 1 / (1 - MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON)
                    val normalisedScroll =
                        (scrollPercent - MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON) * factor
                    1 - normalisedScroll
                }
                addButton.setScale(fabScale)
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

        titleLayout.setOnClickListener {
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

        learnButton.setOnClickListener {
            val learnSet = viewModel.getLearnSet()
            navigateSafe(
                LanguageFragmentDirections.actionLanguageFragmentToLearnFragment(learnSet)
            )
        }
    }

    private fun onTagExistingClick(entryItem: VocabEntryEditItem, tag: TagItemProps.Existing) {
        viewModel.deleteTagFromVocabEntry(entryItem, tag)
    }

    private fun onTagSuggestionClick(entryItem: VocabEntryEditItem, suggestion: TagSuggestion) {
        viewModel.addTagSuggestionToVocabEntry(entryItem, suggestion)
    }

    private fun newTagCreateCallback() = object : TagCreateCallback {
        override fun onUpdateName(
            editItem: VocabEntryEditItem,
            text: String,
            selection: TextSelection
        ) {
            viewModel.onAddTagUpdate(editItem, text, selection)
        }
    }

    private fun getLastItemPosition() = vocabEntriesAdapter.itemCount - 1

    companion object {
        const val MIN_SCROLL_PERCENT_TO_TRIGGER_CREATE_FOCUS = 0.99f
        const val MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON = 0.8f
    }
}
