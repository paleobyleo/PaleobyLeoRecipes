package com.leo.paleorecipes.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leo.paleorecipes.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity): Long

    @Query("DELETE FROM ingredients WHERE name = :ingredientName")
    suspend fun deleteIngredient(ingredientName: String)

    @Query("DELETE FROM ingredients")
    suspend fun deleteAllIngredients()
}
