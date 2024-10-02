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
    fun insertAll(vararg references: GiftListGiftCrossReference)

    @Update
    fun update(vararg references:GiftListGiftCrossReference)

    @Delete
    fun delete(reference: GiftListGiftCrossReference)

    @Query("SELECT * FROM GiftListGiftCrossReference")
    fun getAll(): List<GiftListGiftCrossReference>



}