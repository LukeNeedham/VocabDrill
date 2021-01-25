package com.lukeneedham.vocabdrill.presentation.util.extension

import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.config.FeatureDelegateConfig
import com.lukeneedham.flowerpotrecycler.util.extensions.addDelegate
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.delegate.LayoutParamSetupDelegate
import com.lukeneedham.vocabdrill.presentation.util.recyclerview.delegate.OnItemLongClickDelegate

fun <ItemType, ItemViewType : View> FeatureDelegateConfig<ItemType, ItemViewType>.addOnItemLongClickListener(
    listener: (item: ItemType) -> Unit
) {
    addDelegate(OnItemLongClickDelegate(listener))
}

fun <ItemType, ItemViewType : View> FeatureDelegateConfig<ItemType, ItemViewType>.setupLayoutParams(
    setupWork: (params: ViewGroup.LayoutParams) -> ViewGroup.LayoutParams
) {
    addDelegate(LayoutParamSetupDelegate(setupWork))
}
