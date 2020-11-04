package com.lukeneedham.vocabdrill.presentation.util.recyclerview.delegate

import android.view.View
import com.lukeneedham.flowerpotrecycler.adapter.ViewHolder
import com.lukeneedham.flowerpotrecycler.adapter.delegates.feature.BaseAdapterFeatureDelegate

class OnItemLongClickDelegate<ItemType, ItemViewType : View>(
    private val listener: (item: ItemType) -> Unit
) :
    BaseAdapterFeatureDelegate<ItemType, ItemViewType>() {
    override fun onViewHolderBound(
        holder: ViewHolder<ItemViewType>,
        position: Int,
        itemView: ItemViewType,
        item: ItemType
    ) {
        itemView.setOnLongClickListener {
            listener(item)
            true
        }
    }
}
