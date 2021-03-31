package com.lukeneedham.vocabdrill.presentation.util.recyclerview

import androidx.recyclerview.widget.DiffUtil

class ListDiffCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>,
        private val itemCallback: DiffUtil.ItemCallback<T>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            itemCallback.areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            itemCallback.areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }
