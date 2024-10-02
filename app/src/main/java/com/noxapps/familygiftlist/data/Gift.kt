package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@Entity
data class Gift(
    @PrimaryKey(autoGenerate = true)val giftId: Int,
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
}