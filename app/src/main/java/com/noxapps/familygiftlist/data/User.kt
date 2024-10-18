package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class User (
    @PrimaryKey val userId:String,
    val email:String,
    var firstName:String,
    var lastName:String,
    var birthday: LocalDate,
    //var profilePicture: String
){
}