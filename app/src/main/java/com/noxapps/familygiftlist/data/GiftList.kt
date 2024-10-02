package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GiftList(
    @PrimaryKey(autoGenerate = true) val listId:Int,
    val creatorID: Int,
    val creatorName: String,
    var listName: String,
    var description:String
) {
}