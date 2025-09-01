package com.leo.paleorecipes.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for recipe search by ingredients
 */
data class RecipeResponse(
    val id: Int,
    val title: String,
    val image: String?,
    @SerializedName("missedIngredientCount")
    val missedIngredientCount: Int,
    @SerializedName("usedIngredients")
    val usedIngredients: List<Ingredient>,
    @SerializedName("missedIngredients")
    val missedIngredients: List<Ingredient>,
    @SerializedName("unusedIngredients")
    val unusedIngredients: List<Ingredient>,
)

/**
 * Ingredient model for API responses
 */
data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val image: String?,
    val original: String? = null,
)

/**
 * Detailed recipe information response model
 */
data class RecipeDetailResponse(
    val id: Int,
    val title: String,
    val image: String?,
    val summary: String,
    val instructions: String,
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<ExtendedIngredient>,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    val servings: Int,
)

/**
 * Extended ingredient information for detailed recipe
 */
data class ExtendedIngredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String,
    val image: String?,
)

/**
 * Domain model for recipe data
 */
data class DomainRecipe(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTime: Int,
    val cookTime: Int,
    val servings: Int,
    val category: String = "",
    val difficulty: String = "",
    val notes: String = "",
    val imageUri: String = "",
    val isFavorite: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
)
