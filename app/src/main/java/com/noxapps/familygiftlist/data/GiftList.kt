package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GiftList(
    @PrimaryKey(autoGenerate = true) val listId:Int,
    val creatorID: String,
    val creatorName: String,
    var listName: String,
    var description:String
) {
    constructor(oldList:GiftList, newId:Int) : this(
        listId=newId,
        creatorID = oldList.creatorID,
        creatorName = oldList.creatorName,
        listName = oldList.listName,
        description = oldList.description
    )
}