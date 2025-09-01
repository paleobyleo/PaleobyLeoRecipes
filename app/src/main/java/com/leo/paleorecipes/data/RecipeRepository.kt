package com.leo.paleorecipes.data

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeRepository(private val context: Context) {

    private val recipeDao: RecipeDao
    val allPaleoRecipes: LiveData<List<Recipe>>
    val allUserRecipes: LiveData<List<Recipe>>

    init {
        // Get the database and DAO
        val database = RecipeDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO))
        recipeDao = database.recipeDao()

        // Initialize LiveData
        allPaleoRecipes = recipeDao.getAllPaleoRecipes()
        allUserRecipes = recipeDao.getAllUserRecipes()
    }

    suspend fun insert(recipe: Recipe) {
        recipeDao.insert(recipe)
    }

    suspend fun update(recipe: Recipe) {
        recipeDao.update(recipe)
    }

    suspend fun delete(recipe: Recipe) {
        recipeDao.delete(recipe)
    }

    fun getRecipeById(id: Long): LiveData<Recipe> {
        return recipeDao.getRecipeById(id)
    }

    fun getFavoriteRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }

    fun searchUserRecipes(query: String): LiveData<List<Recipe>> {
        return recipeDao.searchUserRecipes(query)
    }

    fun searchPaleoRecipes(query: String): LiveData<List<Recipe>> {
        return recipeDao.searchPaleoRecipes(query)
    }

    fun searchAllRecipes(query: String): LiveData<List<Recipe>> {
        return recipeDao.searchAllRecipes(query)
    }
}