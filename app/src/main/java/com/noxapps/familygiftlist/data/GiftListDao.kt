package com.noxapps.familygiftlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GiftListDao {
    @Insert
    fun insertAll(vararg giftLists: GiftList)

    @Update
    fun update(vararg giftLists:GiftList)

    @Delete
    fun delete(giftList: Gift)

    @Query("SELECT * FROM gift")
    fun getAll(): List<Gift>
}