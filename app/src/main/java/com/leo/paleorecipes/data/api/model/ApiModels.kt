package com.leo.paleorecipes.data.api.model

import com.google.gson.annotations.SerializedName

/**
 * API response model for recipe search results
 */
data class ApiRecipe(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String?,
    @SerializedName("usedIngredients") val usedIngredients: List<ApiIngredient>? = null,
    @SerializedName("missedIngredients") val missedIngredients: List<ApiIngredient>? = null,
    @SerializedName("unusedIngredients") val unusedIngredients: List<ApiIngredient>? = null,
    @SerializedName("readyInMinutes") val readyInMinutes: Int? = null,
    @SerializedName("servings") val servings: Int? = null,
)

/**
 * API response model for detailed recipe information
 */
data class ApiRecipeDetail(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String?,
    @SerializedName("summary") val summary: String?,
    @SerializedName("instructions") val instructions: String?,
    @SerializedName("readyInMinutes") val readyInMinutes: Int?,
    @SerializedName("servings") val servings: Int?,
    @SerializedName("extendedIngredients") val extendedIngredients: List<ApiIngredient>? = null,
    @SerializedName("analyzedInstructions") val analyzedInstructions: List<ApiAnalyzedInstruction>? = null,
)

/**
 * API model for recipe ingredients
 */
data class ApiIngredient(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("original") val original: String?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("unit") val unit: String?,
)

/**
 * API model for analyzed recipe instructions
 */
data class ApiAnalyzedInstruction(
    @SerializedName("name") val name: String?,
    @SerializedName("steps") val steps: List<ApiInstructionStep>?,
)

/**
 * API model for instruction steps
 */
data class ApiInstructionStep(
    @SerializedName("number") val number: Int?,
    @SerializedName("step") val step: String?,
    @SerializedName("ingredients") val ingredients: List<ApiIngredient>?,
    @SerializedName("equipment") val equipment: List<ApiEquipment>?,
)

/**
 * API model for cooking equipment
 */
data class ApiEquipment(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("localizedName") val localizedName: String?,
    @SerializedName("image") val image: String?,
)
