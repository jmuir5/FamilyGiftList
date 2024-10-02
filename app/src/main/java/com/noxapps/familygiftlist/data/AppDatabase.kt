package com.noxapps.familygiftlist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Gift::class, GiftList::class, User::class, GiftListGiftCrossReference::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun giftDao(): GiftDao
    abstract fun giftListDao(): GiftListDao
    abstract fun userDao():UserDao
    abstract fun referenceDao():ReferenceDao
}