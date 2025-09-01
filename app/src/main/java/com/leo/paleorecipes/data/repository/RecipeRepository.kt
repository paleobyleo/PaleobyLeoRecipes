package com.leo.paleorecipes.data.repository

import com.leo.paleorecipes.data.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for recipe data operations.
 * Abstracts the data source from the rest of the app.
 */
interface RecipeRepository {
    /**
     * Get all paleo recipes from the database.
     */
    fun getAllPaleoRecipes(): Flow<List<Recipe>>

    /**
     * Get all user-created recipes from the database.
     */
    fun getAllUserRecipes(): Flow<List<Recipe>>

    /**
     * Get a recipe by its ID.
     */
    suspend fun getRecipeById(id: Long): Recipe?

    /**
     * Observe a recipe by its ID.
     */
    fun observeRecipeById(recipeId: Long): Flow<Recipe?>

    /**
     * Get all favorite recipes.
     */
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    /**
     * Search recipes by query.
     */
    fun searchRecipes(query: String): Flow<List<Recipe>>

    /**
     * Search user recipes by query.
     */
    fun searchUserRecipes(query: String): Flow<List<Recipe>>

    /**
     * Search paleo recipes by query.
     */
    fun searchPaleoRecipes(query: String): Flow<List<Recipe>>

    /**
     * Search all recipes by query.
     */
    fun searchAllRecipes(query: String): Flow<List<Recipe>>

    /**
     * Search recipes by a list of ingredients.
     *
     * @param ingredients The list of ingredients to search for.
     * @return A list of recipes that contain any of the specified ingredients.
     */
    suspend fun searchRecipesByIngredients(ingredients: List<String>): List<Recipe>

    /**
     * Inserts a new recipe into the database.
     *
     * @param recipe The recipe to be inserted.
     * @return The inserted recipe with its generated ID.
     */
    suspend fun insertRecipe(recipe: Recipe): Recipe

    /**
     * Inserts multiple recipes into the database.
     *
     * @param recipes The list of recipes to be inserted.
     */
    suspend fun insertRecipes(recipes: List<Recipe>)

    /**
     * Update a recipe in the database.
     */
    suspend fun updateRecipe(recipe: Recipe)

    /**
     * Delete a recipe from the database.
     */
    suspend fun deleteRecipe(recipeId: Long)

    /**
     * Delete all recipes from the database.
     */
    suspend fun deleteAllRecipes()

    /**
     * Get all recipes synchronously.
     */
    suspend fun getAllRecipesSync(): List<Recipe>

    /**
     * Sync recipes from the remote data source to the local database.
     *
     * @param forceRefresh Whether to force a refresh from the remote data source.
     * @return A Result indicating success or failure of the sync operation.
     */
    suspend fun syncRecipes(forceRefresh: Boolean = false): Result<Unit>
}
