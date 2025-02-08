package com.example.demonstator2_databases.db

import androidx.room.Entity
import androidx.room.PrimaryKey

//to create a table in the database for the shop entity
@Entity(tableName = "inventory")
data class ShopEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var quantity: Int,
    var price: Double
)
