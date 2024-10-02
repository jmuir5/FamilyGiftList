package com.noxapps.familygiftlist.data

import androidx.room.Junction
import androidx.room.Relation
import androidx.room.Embedded

data class ListWithGifts(
    @Embedded val giftList: GiftList,
    @Relation(
        parentColumn = "listId",
        entityColumn = "giftId",
        associateBy = Junction(GiftListGiftCrossReference::class)
    )
    val gifts: List<Gift>
) {
}

data class GiftWithLists(
    @Embedded val gift: Gift,
    @Relation(
        parentColumn = "giftId",
        entityColumn = "listId",
        associateBy = Junction(GiftListGiftCrossReference::class)
    )
    val lists: List<GiftList>
) {
}