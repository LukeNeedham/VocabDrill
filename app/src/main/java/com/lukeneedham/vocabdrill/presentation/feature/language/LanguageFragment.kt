package com.lukeneedham.vocabdrill.presentation.feature.language

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukeneedham.vocabdrill.R
import com.lukeneedham.vocabdrill.domain.model.VocabEntryProto
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagItem
import com.lukeneedham.vocabdrill.presentation.feature.tag.TagSuggestionsView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateCallback
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create.VocabEntryCreateItemView
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing.VocabEntryExistingCallback
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

    private val vocabEntryExistingCallback = object : VocabEntryExistingCallback {
        override fun onWordAChanged(item: VocabEntryItem.Existing, newWordA: String) {
            viewModel.onExistingItemWordAChanged(item, newWordA)
        }

        override fun onWordBChanged(item: VocabEntryItem.Existing, newWordB: String) {
            viewModel.onExistingItemWordBChanged(item, newWordB)
        }

        override fun onDelete(item: VocabEntryItem.Existing) {
            viewModel.deleteEntry(item)
        }
    }

    private val vocabEntryCreateCallback = object : VocabEntryCreateCallback {
        override fun onWordAChanged(item: VocabEntryItem.Create, newWordA: String) {
            viewModel.onCreateItemWordAChanged(item, newWordA)
        }

        override fun onWordBChanged(item: VocabEntryItem.Create, newWordA: String) {
            viewModel.onCreateItemWordBChanged(item, newWordA)
        }

        override fun save(proto: VocabEntryProto) {
            viewModel.save(proto)
        }
    }

    private val tagCreateCallback = object : TagCreateCallback {
        override fun onNameChanged(tagItem: TagItem.Create, name: String) {
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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val scrollPercent = recyclerView.computeVerticalScrollPercent()
                if (focusCreateItemQueued) {
                    if (scrollPercent >= MIN_SCROLL_PERCENT_TO_TRIGGER_CREATE_FOCUS) {
                        focusCreateItemQueued = false
                        getCreateItemView()?.requestFocus()
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
            focusCreateItemQueued = true
            val lastItemPosition = getLastItemPosition()
            val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

            if (lastVisiblePosition == lastItemPosition) {
                getCreateItemView()?.requestFocus()
            } else {
                recyclerView.smoothScrollToPosition(lastItemPosition)
            }
        }
    }

    private fun getCreateItemView(): VocabEntryCreateItemView? {
        val view = recyclerView.findViewHolderForAdapterPosition(getLastItemPosition())?.itemView
        return view as? VocabEntryCreateItemView
    }

    private fun getLastItemPosition() = vocabEntriesAdapter.itemCount - 1

    companion object {
        const val MIN_SCROLL_PERCENT_TO_TRIGGER_CREATE_FOCUS = 0.99f
        const val MIN_SCROLL_PERCENT_TO_HIDE_ADD_BUTTON = 0.8f
    }
}
