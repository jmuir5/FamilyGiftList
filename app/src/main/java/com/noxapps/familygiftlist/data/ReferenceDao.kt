package com.noxapps.familygiftlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ReferenceDao {
    @Insert
    suspend fun insertAll(vararg references: GiftListGiftCrossReference)

    @Update
    suspend fun update(vararg references:GiftListGiftCrossReference)

    @Delete
    suspend fun delete(reference: GiftListGiftCrossReference)

    @Query("SELECT * FROM GiftListGiftCrossReference")
    suspend fun getAll(): List<GiftListGiftCrossReference>

    @Query("SELECT * FROM GiftListGiftCrossReference WHERE listId = :id")
    suspend fun getByListId(id:Int):List<GiftListGiftCrossReference>

    @Query("SELECT * FROM GiftListGiftCrossReference WHERE giftId = :id")
    suspend fun getByGiftId(id:Int):List<GiftListGiftCrossReference>


}