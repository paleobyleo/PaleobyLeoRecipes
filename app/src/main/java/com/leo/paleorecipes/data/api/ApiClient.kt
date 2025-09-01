package com.leo.paleorecipes.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiClient @Inject constructor(
    private val spoonacularService: SpoonacularApiService,
) {

    // API key should be moved to secure storage in production
    companion object {
        const val API_KEY = "2695dd0bbcd640de8e73bff71ebf16fa"
    }

    suspend fun searchRecipesByIngredients(
        ingredients: String,
        number: Int = 10,
        ignorePantry: Boolean = true,
        ranking: Int = 1,
    ) = withContext(Dispatchers.IO) {
        spoonacularService.searchRecipesByIngredients(
            ingredients = ingredients,
            number = number,
            apiKey = API_KEY,
            ignorePantry = ignorePantry,
            ranking = ranking,
        )
    }

    suspend fun getRecipeInformation(
        id: Long,
        includeNutrition: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        spoonacularService.getRecipeInformation(
            id = id,
            apiKey = API_KEY,
            includeNutrition = includeNutrition,
        )
    }

    fun getApiService(): SpoonacularApiService = spoonacularService
}
