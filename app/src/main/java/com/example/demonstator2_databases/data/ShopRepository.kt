package com.example.demonstator2_databases.data

import com.example.demonstator2_databases.db.InventoryDao
import com.example.demonstator2_databases.db.ShopEntity
import kotlinx.coroutines.flow.Flow

//this class is used to interact with the database while the viewmodel interacts with the repository
class ShopRepository(private val inventoryDao: InventoryDao) {

    val allItems: Flow<List<ShopEntity>> = inventoryDao.getAllItems()

    suspend fun insert(item: ShopEntity) {
        inventoryDao.insert(item)
    }
    suspend fun delete(item: ShopEntity) {
        inventoryDao.delete(item)
    }
    suspend fun update(item: ShopEntity) {
        inventoryDao.update(item)
    }

    suspend fun isFruitNameExists(name: String): Boolean {
        return inventoryDao.getItemByName(name) != null
    }

}