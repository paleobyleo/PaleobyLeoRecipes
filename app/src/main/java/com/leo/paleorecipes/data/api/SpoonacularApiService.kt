package com.leo.paleorecipes.data.api

import com.leo.paleorecipes.data.api.model.ApiRecipe
import com.leo.paleorecipes.data.api.model.ApiRecipeDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApiService {
    @GET("recipes/findByIngredients")
    suspend fun searchRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String,
        @Query("ignorePantry") ignorePantry: Boolean = true,
        @Query("ranking") ranking: Int = 1,
    ): List<ApiRecipe>

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Path("id") id: Long,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = false,
    ): ApiRecipeDetail
}
