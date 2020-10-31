package com.lukeneedham.vocabdrill.presentation.util.recyclerview

object LinearMarginItemDecorationCreator {
    fun fromHorizontal(
        leftEdgeMargin: Int,
        betweenMargin: Int,
        rightEdgeMargin: Int,
    ) = LinearMarginItemDecoration(
        0,
        0,
        leftEdgeMargin,
        rightEdgeMargin,
        0,
        0,
        betweenMargin,
        betweenMargin
    )

    fun fromVertical(
        topEdgeMargin: Int,
        betweenMargin: Int,
        bottomEdgeMargin: Int,
    ) = LinearMarginItemDecoration(
        topEdgeMargin,
        bottomEdgeMargin,
        0,
        0,
        betweenMargin,
        betweenMargin,
        0,
        0
    )
}
