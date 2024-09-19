package com.noxapps.familygiftlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GiftDao {
    @Insert
    fun insertAll(vararg gifts: Gift)

    @Update
    fun update(vararg gifts:Gift)

    @Delete
    fun delete(gift: Gift)

    @Query("SELECT * FROM gift")
    fun getAll(): List<Gift>
}