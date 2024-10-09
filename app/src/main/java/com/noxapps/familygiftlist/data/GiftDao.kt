package com.noxapps.familygiftlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface GiftDao {
    @Insert
    suspend fun insertAll(vararg gifts: Gift)

    @Insert
    suspend fun insert(gift:Gift)

    @Update
    suspend fun update(vararg gifts:Gift)

    @Delete
    suspend fun delete(gift: Gift)

    @Query("SELECT * FROM Gift")
    suspend fun getAll(): List<Gift>

    @Query ("SELECT * FROM giftList WHERE listId = :id LIMIT 1")
    suspend fun getOneById (id: Int) : GiftList


    @Transaction
    @Query("SELECT * FROM Gift")
    suspend fun getGiftsWithLists(): List<GiftWithLists>


}