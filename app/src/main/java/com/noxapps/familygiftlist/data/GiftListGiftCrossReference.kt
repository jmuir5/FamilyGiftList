package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["listId", "giftId"])
data class GiftListGiftCrossReference(
    val listId:Int,
    val giftId:Int,
) {
}
