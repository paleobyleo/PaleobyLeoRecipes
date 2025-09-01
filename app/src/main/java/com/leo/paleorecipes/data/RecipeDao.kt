package com.leo.paleorecipes.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE isUserCreated = 0 ORDER BY title ASC")
    fun getAllPaleoRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isUserCreated = 1 ORDER BY title ASC")
    fun getAllUserRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Long): LiveData<Recipe>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isUserCreated = 1 AND title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchUserRecipes(query: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isUserCreated = 0 AND title LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchPaleoRecipes(query: String): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY title ASC")
    fun searchAllRecipes(query: String): LiveData<List<Recipe>>
}