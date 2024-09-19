package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class GiftList(
    @PrimaryKey(autoGenerate = true) val id:Int,
    val creatorID: Int,
    val creator: String,
    var listName: String,
    var list: List<Gift>
) {
}