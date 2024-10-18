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

data class UserWithGifts(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "creatorId",
    )
    val gifts:List<Gift>
)

data class UserWithGiftsWithLists(
    @Embedded val user: User,
    @Relation(
        entity = Gift::class,
        parentColumn = "userId",
        entityColumn = "creatorId",
    )
    val giftsWithLists: List<GiftWithLists>
)

data class UserWithLists(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "creatorId",
    )
    val lists:List<GiftList>
)

data class UserWithListsWithGifts(
    @Embedded val user: User,
    @Relation(
        entity = GiftList::class,
        parentColumn = "userId",
        entityColumn = "creatorId",
    )
    val listsWithGifts: List<ListWithGifts>
)