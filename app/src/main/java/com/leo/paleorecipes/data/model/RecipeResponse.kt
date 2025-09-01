package com.leo.paleorecipes.data.model

import com.google.gson.annotations.SerializedName

/**
 * Main recipe response model from Spoonacular API
 */
data class Recipe(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("missedIngredientCount")
    val missedIngredientCount: Int = 0,
    @SerializedName("usedIngredients")
    val usedIngredients: List<Ingredient> = emptyList(),
    @SerializedName("missedIngredients")
    val missedIngredients: List<Ingredient> = emptyList(),
    @SerializedName("unusedIngredients")
    val unusedIngredients: List<Ingredient> = emptyList(),
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int? = null,
    @SerializedName("servings")
    val servings: Int? = null,
    @SerializedName("sourceUrl")
    val sourceUrl: String? = null,
    @SerializedName("summary")
    val summary: String? = null,
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<ExtendedIngredient>? = null,
    @SerializedName("analyzedInstructions")
    val analyzedInstructions: List<AnalyzedInstruction>? = null,
    @SerializedName("vegetarian")
    val isVegetarian: Boolean = false,
    @SerializedName("vegan")
    val isVegan: Boolean = false,
    @SerializedName("glutenFree")
    val isGlutenFree: Boolean = false,
    @SerializedName("dairyFree")
    val isDairyFree: Boolean = false,
)

/**
 * Detailed recipe information model from Spoonacular API
 */
data class RecipeDetail(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("instructions")
    val instructions: String,
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<ExtendedIngredient>,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("servings")
    val servings: Int,
    @SerializedName("vegetarian")
    val isVegetarian: Boolean = false,
    @SerializedName("vegan")
    val isVegan: Boolean = false,
    @SerializedName("glutenFree")
    val isGlutenFree: Boolean = false,
    @SerializedName("dairyFree")
    val isDairyFree: Boolean = false,
)

/**
 * Analyzed instruction model for recipe steps
 */
data class AnalyzedInstruction(
    @SerializedName("name")
    val name: String?,
    @SerializedName("steps")
    val steps: List<Step>,
)

/**
 * Step in a recipe instruction
 */
data class Step(
    @SerializedName("number")
    val number: Int,
    @SerializedName("step")
    val step: String,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient> = emptyList(),
    @SerializedName("equipment")
    val equipment: List<Equipment> = emptyList(),
    @SerializedName("length")
    val length: Length? = null,
)

/**
 * Length of time for a step
 */
data class Length(
    @SerializedName("number")
    val number: Int,
    @SerializedName("unit")
    val unit: String,
)

/**
 * Equipment used in a recipe
 */
data class Equipment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("localizedName")
    val localizedName: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("temperature")
    val temperature: Temperature? = null,
)

/**
 * Temperature information for equipment
 */
data class Temperature(
    @SerializedName("number")
    val number: Double,
    @SerializedName("unit")
    val unit: String,
)
