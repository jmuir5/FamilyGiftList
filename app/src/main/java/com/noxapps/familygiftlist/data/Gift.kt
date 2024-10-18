package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@Entity
data class Gift(
    @PrimaryKey(autoGenerate = true)val giftId: Int,
    var creatorId: String = "",
    var name: String ="",
    var description: String = "",
    var link:String = "",
    var price: Int = 0,
    var reserved: Boolean = false,
    var reservedDate: LocalDate,
    var reservedBy: String = "",
    var purchased: Boolean = false,
    var purchaseDate: LocalDate,
    var purchasedBy: String = "",
    var purchaseProof: String = ""
    ){
    constructor(oldGift:Gift, newId:Int) : this(
        giftId = newId,
        creatorId = oldGift.creatorId,
        name = oldGift.name,
        description = oldGift.description ,
        link= oldGift.link,
        price = oldGift.price,
        reserved = oldGift.reserved,
        reservedDate = oldGift.reservedDate,
        reservedBy = oldGift.reservedBy,
        purchased = oldGift.purchased,
        purchaseDate = oldGift.purchaseDate,
        purchasedBy = oldGift.purchasedBy,
        purchaseProof = oldGift.purchaseProof
    )
}