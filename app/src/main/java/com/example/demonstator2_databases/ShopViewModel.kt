package com.example.demonstator2_databases

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.demonstator2_databases.db.ShopDatabase
import com.example.demonstator2_databases.db.ShopEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FruitViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ShopDatabase.getDatabase(application).inventoryDao()
    val allFruits: Flow<List<ShopEntity>> = dao.getAllItems()

    fun addFruit(fruit: ShopEntity) {
        viewModelScope.launch {
            dao.insert(fruit)
        }
    }

    fun removeFruit(fruit: ShopEntity) {
        viewModelScope.launch {
            dao.delete(fruit)
        }
    }

    fun updateFruit(fruit: ShopEntity) {
        viewModelScope.launch {
            dao.update(fruit)
        }
    }

    // Check if a fruit with the given name exists
    suspend fun isFruitNameExists(name: String): Boolean {
        return dao.getItemByName(name) != null

    }
}