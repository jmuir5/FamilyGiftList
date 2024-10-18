package com.noxapps.familygiftlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insertAll(vararg users: User)

    @Insert
    suspend fun insertOne(user: User)

    @Update
    suspend fun update(vararg users:User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query ("SELECT * FROM user WHERE userId = :id LIMIT 1")
    suspend fun getOneById (id: String) : User

    @Transaction
    @Query ("SELECT * FROM user WHERE userId = :id LIMIT 1")
    suspend fun getOneWithGiftsById (id: String) : UserWithGifts

    @Transaction
    @Query ("SELECT * FROM user WHERE userId = :id LIMIT 1")
    suspend fun getOneWithListsById (id: String) : UserWithLists

    @Transaction
    @Query ("SELECT * FROM user WHERE userId = :id LIMIT 1")
    suspend fun getOneWithGiftsAndListsById (id: String) : UserWithGiftsWithLists

    @Transaction
    @Query ("SELECT * FROM user WHERE userId = :id LIMIT 1")
    suspend fun getOneWithListsAndGiftsById (id: String) : UserWithListsWithGifts
}