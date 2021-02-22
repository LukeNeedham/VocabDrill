package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.existing

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryViewProps

data class VocabEntryExistingProps(
    override val entryItem: VocabEntryEditItem.Existing,
    val viewMode: ViewMode
) : VocabEntryViewProps
