package com.noxapps.familygiftlist.data

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDBInteractor {
    companion object {
        private val firebaseDB = Firebase.database.reference
        fun upsertUser(user: User){
            firebaseDB
                .child(user.userId)
                .child("User")
                .setValue(user)
        }
        fun getUser():User{
            return User()
        }

        fun upsertGift(){

        }
        fun getGift():Gift{
            return Gift()
        }

        fun deleteGift() {
        }

        fun upsertList() {
        }

        fun getList():GiftList{
            return GiftList()
        }

        fun deleteList() {
        }
    }
}
