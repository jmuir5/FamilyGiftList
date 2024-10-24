package com.noxapps.familygiftlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftListDao {
    @Insert
    suspend fun insertAll(vararg giftLists: GiftList)

    @Insert
    suspend fun insert(giftList:GiftList):Long

    @Upsert
    suspend fun upsert(giftList:GiftList):Long

    @Update
    suspend fun update(vararg giftLists:GiftList)

    @Delete
    suspend fun delete(giftList: GiftList)

    @Query("SELECT * FROM giftList")
    suspend fun getAll(): List<GiftList>

    /*@Query(
        "SELECT * FROM giftList " +
        "JOIN Gift ON giftList.id = gift.listID"
    )
    fun getAllWithGifts(): Map<GiftList, List<Gift>>*/

    @Query("SELECT * FROM giftList WHERE listId = :id")
    suspend fun getById(id:Int):List<GiftList>

    @Query ("SELECT * FROM giftList WHERE listId = :id LIMIT 1")
    suspend fun getOneById (id: Int) : GiftList

    @Transaction
    @Query("SELECT * FROM giftList")
    suspend fun getGiftsWithLists(): List<ListWithGifts>

    @Transaction
    @Query ("SELECT * FROM giftList WHERE listId = :id LIMIT 1")
    suspend fun getOneWithListsById (id: Int) : ListWithGifts


}