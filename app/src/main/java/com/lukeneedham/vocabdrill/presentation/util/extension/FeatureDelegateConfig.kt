package com.lukeneedham.vocabdrill.presentation.util.extension

import android.view.View
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureDelegateConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addDelegate
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.delegate.OnItemLongClickDelegate

fun <ItemType, ItemViewType : View> FeatureDelegateConfig<ItemType, ItemViewType>.addOnItemLongClickListener(
    listener: (item: ItemType) -> Unit
) {
    addDelegate(OnItemLongClickDelegate(listener))
}
