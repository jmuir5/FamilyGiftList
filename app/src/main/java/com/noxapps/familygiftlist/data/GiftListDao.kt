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
    fun delete(giftList: GiftList)

    @Query("SELECT * FROM giftList")
    fun getAll(): List<GiftList>

    @Query(
        "SELECT * FROM giftList " +
        "JOIN Gift ON giftList.id = gift.listID"
    )
    fun getAllWithGifts(): Map<GiftList, List<Gift>>

    @Query("SELECT * FROM giftlist WHERE id = :id")
    fun getById(id:Int):List<GiftList>
}