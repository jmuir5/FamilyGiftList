package com.noxapps.familygiftlist.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Gift::class, GiftList::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun giftList(): GiftListDao
}