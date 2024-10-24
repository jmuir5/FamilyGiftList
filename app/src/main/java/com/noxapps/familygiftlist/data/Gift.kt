package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@Entity
data class Gift(
    @PrimaryKey(autoGenerate = true)val giftId: Int = 0,
    var creatorId: String = "",
    var name: String = "",
    var description: String = "",
    var link:String = "",
    var price: Int = 0,
    var reserved: Boolean = false,
    var _reservedDate: String = "",
    var reservedBy: String = "",
    var purchased: Boolean = false,
    var _purchasedDate: String = "",
    var purchasedBy: String = "",
    var purchaseProof: String = ""
    ){
    var reservedDate:LocalDate
        @Exclude get() {return LocalDate.parse(_reservedDate)}
        set(value) {
            _reservedDate = value.toString()
        }
    var purchasedDate:LocalDate
        @Exclude get() {return LocalDate.parse(_purchasedDate)}
        set(value) {
            _purchasedDate = value.toString()
        }


    /*fun getReservedDate():LocalDate{
        return LocalDate.parse(this.reservedDate)
    }
    fun setReservedDate(date:LocalDate){
        this.reservedDate = date.toString()
    }
    fun getPurchaseDate():LocalDate{
        return LocalDate.parse(this.purchaseDate)
    }
    fun setPurchaseDate(date:LocalDate){
        this.purchaseDate = date.toString()
    }*/

    constructor(oldGift:Gift, newId:Int) : this(
        giftId = newId,
        creatorId = oldGift.creatorId,
        name = oldGift.name,
        description = oldGift.description ,
        link= oldGift.link,
        price = oldGift.price,
        reserved = oldGift.reserved,
        _reservedDate = oldGift._reservedDate,
        reservedBy = oldGift.reservedBy,
        purchased = oldGift.purchased,
        _purchasedDate = oldGift._purchasedDate,
        purchasedBy = oldGift.purchasedBy,
        purchaseProof = oldGift.purchaseProof
    )
}