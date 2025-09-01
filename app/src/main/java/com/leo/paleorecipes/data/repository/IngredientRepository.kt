package com.leo.paleorecipes.data.repository

import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    suspend fun addIngredient(ingredient: String)
    suspend fun removeIngredient(ingredient: String)
    fun getIngredients(): Flow<List<String>>
}
