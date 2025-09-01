package com.leo.paleorecipes.network

import com.leo.paleorecipes.data.model.Recipe
import com.leo.paleorecipes.data.model.RecipeDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularService {
    @GET("recipes/findByIngredients")
    suspend fun searchRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String,
        @Query("ignorePantry") ignorePantry: Boolean = true,
        @Query("ranking") ranking: Int = 1,
    ): List<Recipe>

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = false,
    ): RecipeDetail
}

data class RecipeDetailResponse(
    val id: Int,
    val title: String,
    val image: String?,
    val summary: String,
    val instructions: String,
    val extendedIngredients: List<ExtendedIngredient>,
    val readyInMinutes: Int,
    val servings: Int,
)

data class ExtendedIngredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val image: String?,
)
