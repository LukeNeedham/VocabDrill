package com.lukeneedham.vocabdrill.presentation.util.recyclerview.delegate

import android.view.View
import android.view.ViewGroup
import com.lukeneedham.flowerpotrecycler.adapter.ViewHolder
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.BaseAdapterFeatureDelegate


class LayoutParamSetupDelegate<ItemType, ItemViewType : View>(
    /** provides params, the current params, and return the new params */
    private val setupWork: (params: ViewGroup.LayoutParams) -> ViewGroup.LayoutParams
) : BaseAdapterFeatureDelegate<ItemType, ItemViewType>() {

    override fun onViewHolderCreated(
        parent: ViewGroup,
        viewType: Int,
        viewHolder: ViewHolder<ItemViewType>,
        itemView: ItemViewType
    ) {
        itemView.layoutParams = setupWork(itemView.layoutParams)
    }
}
