package com.leo.paleorecipes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<Recipe>): List<Long>

    // Update operations
    @Update
    suspend fun update(recipe: Recipe): Int

    @Update
    suspend fun updateAll(recipes: List<Recipe>): Int

    // Delete operations
    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteById(recipeId: Long): Int

    @Query("DELETE FROM recipes")
    suspend fun deleteAll(): Int

    // Query operations
    @Query("SELECT * FROM recipes WHERE isUserCreated = 0 ORDER BY title ASC")
    fun getAllPaleoRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isUserCreated = 1 ORDER BY title ASC")
    fun getAllUserRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?

    @Query(
        """
        SELECT * FROM recipes
        WHERE title LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY title ASC
    """,
    )
    @RewriteQueriesToDropUnusedColumns
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun observeRecipeById(recipeId: Long): Flow<Recipe?>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    @Query(
        """
        SELECT * FROM recipes
        WHERE isUserCreated = 1
        AND title LIKE '%' || :query || '%'
        ORDER BY title ASC
    """,
    )
    @RewriteQueriesToDropUnusedColumns
    fun searchUserRecipes(query: String): Flow<List<Recipe>>

    @Query(
        """
        SELECT * FROM recipes
        WHERE isUserCreated = 0
        AND title LIKE '%' || :query || '%'
        ORDER BY title ASC
    """,
    )
    @RewriteQueriesToDropUnusedColumns
    fun searchPaleoRecipes(query: String): Flow<List<Recipe>>

    @Query(
        """
        SELECT * FROM recipes
        WHERE title LIKE '%' || :query || '%'
        OR ingredients LIKE '%' || :query || '%'
        ORDER BY title ASC
    """,
    )
    @RewriteQueriesToDropUnusedColumns
    fun searchAllRecipes(query: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesSync(): List<Recipe>

    // Transaction operations
    @Transaction
    suspend fun insertWithTransaction(recipe: Recipe): Long {
        return insert(recipe)
    }

    @Transaction
    suspend fun updateWithTransaction(recipe: Recipe): Int {
        return update(recipe)
    }

    @Transaction
    suspend fun deleteWithTransaction(recipeId: Long): Int {
        return delete(recipeId)
    }
}
