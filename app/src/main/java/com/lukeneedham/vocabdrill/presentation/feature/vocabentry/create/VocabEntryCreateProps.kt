package com.lukeneedham.vocabdrill.presentation.feature.vocabentry.create

import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.ViewMode
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryEditItem
import com.lukeneedham.vocabdrill.presentation.feature.vocabentry.VocabEntryViewProps

data class VocabEntryCreateProps(
    override val entryItem: VocabEntryEditItem.Create,
    val viewMode: ViewMode
) : VocabEntryViewProps
