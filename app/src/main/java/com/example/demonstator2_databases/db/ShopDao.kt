package com.example.demonstator2_databases.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Insert
    suspend fun insert(shopEntity: ShopEntity)

    @Query("SELECT * FROM inventory")
    fun getAllItems(): Flow<List<ShopEntity>>

    @Delete
    suspend fun delete(shopEntity: ShopEntity)

    @Update
    suspend fun update(shopEntity: ShopEntity)

    @Query("SELECT * FROM inventory WHERE name = :name LIMIT 1")
    suspend fun getItemByName(name: String): ShopEntity?
    //name limit 1 means that it will return only one item with the name!

}

//functions in dao are meant to do the CRUD operations while the functions in repository are meant to do the business logic
//in simple dao is for database operations and repository is responsible for business logic
//so each function in repository will call a function in dao to do the database operation!!