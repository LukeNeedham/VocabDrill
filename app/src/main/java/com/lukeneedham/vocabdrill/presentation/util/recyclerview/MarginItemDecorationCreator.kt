package com.lukeneedham.vocabdrill.presentation.util.recyclerview

object MarginItemDecorationCreator {
    fun fromHorizontal(
        firstMargin: Int,
        betweenMargin: Int,
        lastMargin: Int,
    ) = object : MarginItemDecoration() {
        override val firstVerticalMargin = 0
        override val betweenVerticalMargin = 0
        override val lastVerticalMargin = 0
        override val firstHorizontalMargin = firstMargin
        override val betweenHorizontalMargin = betweenMargin
        override val lastHorizontalMargin = lastMargin
    }

    fun fromVertical(
        firstMargin: Int,
        betweenMargin: Int,
        lastMargin: Int,
    ) = object : MarginItemDecoration() {
        override val firstVerticalMargin = firstMargin
        override val betweenVerticalMargin = betweenMargin
        override val lastVerticalMargin = lastMargin
        override val firstHorizontalMargin = 0
        override val betweenHorizontalMargin = 0
        override val lastHorizontalMargin = 0
    }
}
