package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["listId", "giftId"])
data class GiftListGiftCrossReference(
    val listId:Int,
    val giftId:Int,
) {
}

@Entity(primaryKeys = ["userId", "listId"])
data class UserGiftListCrossReference(
    val userId:Int,
    val listId:Int,
) {
}


/*
class group object
val name
val description
val list<member Lists>

class memberList
val userId
val listId

firebase:
groups
L->group id
    L-> name
        description
        adminid
        members/lists / memberlists
        L->userID
            L->listid


 */