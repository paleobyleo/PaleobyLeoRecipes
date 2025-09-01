package com.leo.paleorecipes.network

import com.leo.paleorecipes.data.model.Recipe
import com.leo.paleorecipes.data.model.RecipeDetail
import javax.inject.Inject

class SpoonacularApi @Inject constructor(
    private val service: SpoonacularService,
) {
    suspend fun searchRecipesByIngredients(
        ingredients: String,
        number: Int = 10,
        apiKey: String,
        ignorePantry: Boolean = true,
        ranking: Int = 1,
    ): List<Recipe> {
        return service.searchRecipesByIngredients(
            ingredients = ingredients,
            number = number,
            apiKey = apiKey,
            ignorePantry = ignorePantry,
            ranking = ranking,
        )
    }

    suspend fun getRecipeInformation(
        id: Int,
        apiKey: String,
        includeNutrition: Boolean = false,
    ): RecipeDetail {
        return service.getRecipeInformation(
            id = id,
            apiKey = apiKey,
            includeNutrition = includeNutrition,
        )
    }
}
