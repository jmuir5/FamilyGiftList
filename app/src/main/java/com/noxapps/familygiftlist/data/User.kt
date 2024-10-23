package com.noxapps.familygiftlist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import java.time.Instant
import java.time.LocalDate
//import java.util.Date

@Entity
data class User (
    @PrimaryKey val userId:String="",
    val email:String = "",
    var firstName:String = "",
    var lastName:String = "",
    var _birthday: String = LocalDate.now().toString(),
    //var profilePicture: String
){
     var birthday:LocalDate
     @Exclude get() {return LocalDate.parse(_birthday)}
        set(value) {_birthday = value.toString()}
}