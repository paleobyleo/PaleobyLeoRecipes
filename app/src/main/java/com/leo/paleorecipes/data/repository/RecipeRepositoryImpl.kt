package com.leo.paleorecipes.data.repository

import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.data.RecipeDao
import com.leo.paleorecipes.data.api.ApiClient
import com.leo.paleorecipes.utils.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [RecipeRepository] that uses Room database as the data source.
 */
@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val apiClient: ApiClient,
    private val networkMonitor: NetworkMonitor,
) : RecipeRepository {

    override fun getAllPaleoRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllPaleoRecipes()
    }

    override fun getAllUserRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllUserRecipes()
    }

    override suspend fun getRecipeById(id: Long): Recipe? {
        return recipeDao.getRecipeById(id)
    }

    override fun observeRecipeById(recipeId: Long): Flow<Recipe?> {
        return recipeDao.observeRecipeById(recipeId)
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query)
    }

    override fun searchUserRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchUserRecipes(query)
    }

    override fun searchPaleoRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchPaleoRecipes(query)
    }

    override fun searchAllRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchAllRecipes(query)
    }

    override suspend fun insertRecipe(recipe: Recipe): Recipe {
        val id = recipeDao.insert(recipe)
        return recipe.copy(id = id)
    }

    override suspend fun insertRecipes(recipes: List<Recipe>) {
        recipeDao.insertAll(recipes)
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.update(recipe)
    }

    override suspend fun deleteRecipe(recipeId: Long) {
        recipeDao.delete(recipeId)
    }

    override suspend fun deleteAllRecipes() {
        recipeDao.deleteAll()
    }

    override suspend fun getAllRecipesSync(): List<Recipe> {
        return recipeDao.getAllRecipesSync()
    }

    override suspend fun syncRecipes(forceRefresh: Boolean): Result<Unit> {
        return try {
            // Check network availability - using NetworkMonitor's isOnline property
            val isConnected = networkMonitor.isOnline.first()
            if (!isConnected) {
                return Result.failure(IOException("No network connection available"))
            }

            // Get all paleo recipes from the API
            val apiResponse = apiClient.searchRecipesByIngredients(
                ingredients = "chicken,vegetables,spices",
                number = 50,
                ignorePantry = true,
                ranking = 1,
            )

            // Convert API models to domain models and save to local database
            val recipes = apiResponse.map { apiRecipe ->
                // Map ingredients - use usedIngredients if available, otherwise empty list
                val ingredients = apiRecipe.usedIngredients?.mapNotNull {
                    it.name?.takeIf { name -> name.isNotBlank() }
                }?.distinct() ?: emptyList()

                // Create a new Recipe with the correct parameters
                Recipe(
                    id = 0, // Let Room auto-generate the ID
                    title = apiRecipe.title ?: "Untitled Recipe",
                    description = "", // Will be populated when viewing recipe details
                    ingredients = ingredients,
                    instructions = emptyList(), // Will be populated when viewing recipe details
                    prepTime = 0, // Default value
                    cookTime = apiRecipe.readyInMinutes ?: 0,
                    servings = apiRecipe.servings ?: 4,
                    imageUrl = apiRecipe.image ?: "",
                    category = "Paleo",
                    difficulty = "Medium",
                    notes = "",
                    isUserCreated = false,
                    isFavorite = false,
                    dateAdded = System.currentTimeMillis(),
                )
            }

            // Save to local database
            recipeDao.insertAll(recipes)

            Result.success(Unit)
        } catch (e: Exception) {
            // Log the error
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe> {
        if (ingredients.isEmpty()) return emptyList()

        // Create a query to find recipes that contain any of the ingredients
        val query = "%${ingredients.joinToString("%' OR ingredients LIKE '%")}%"

        // Get all recipes and filter them locally for better search functionality
        val allRecipes = recipeDao.getAllRecipesSync()

        return allRecipes.filter { recipe ->
            ingredients.any { ingredient ->
                recipe.ingredients.any { it.contains(ingredient, ignoreCase = true) }
            }
        }
    }
}
