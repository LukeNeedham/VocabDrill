package com.lukeneedham.vocabdrill.presentation.util.extension

import android.view.View
import com.lukeneedham.flowerpotrecycler.adapter.DelegatedRecyclerAdapter

fun <A : Any, B : View> DelegatedRecyclerAdapter<A, B>.getItems(): List<A> =
    positionDelegate.getItems()
