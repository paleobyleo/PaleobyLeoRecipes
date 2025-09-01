package com.leo.paleorecipes.data.repository

import com.leo.paleorecipes.data.local.dao.IngredientDao
import com.leo.paleorecipes.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientRepositoryImpl @Inject constructor(
    private val ingredientDao: IngredientDao,
) : IngredientRepository {

    override suspend fun addIngredient(ingredient: String) {
        ingredientDao.insertIngredient(IngredientEntity(name = ingredient))
    }

    override suspend fun removeIngredient(ingredient: String) {
        ingredientDao.deleteIngredient(ingredient)
    }

    override fun getIngredients(): Flow<List<String>> {
        return ingredientDao.getAllIngredients().map { ingredients ->
            ingredients.map { it.name }
        }
    }
}
