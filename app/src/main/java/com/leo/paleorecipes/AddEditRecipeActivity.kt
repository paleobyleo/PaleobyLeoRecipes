package com.leo.paleorecipes

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.ui.recipe.AddEditRecipeScreen
import com.leo.paleorecipes.ui.theme.PaleoRecipesTheme
import com.leo.paleorecipes.utils.AdUtils
import com.leo.paleorecipes.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class AddEditRecipeActivity : ComponentActivity() {

    private val TAG = "AddEditRecipeActivity"
    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate started")

        // Load an interstitial ad for later use
        AdUtils.loadInterstitialAd(this)

        // Use the correct method based on Android API level
        val editingRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("recipe", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Recipe>("recipe")
        }

        // Check for OCR recipe data
        val ocrRecipeData = intent.getStringExtra("ocr_recipe")

        Log.d(TAG, "Editing recipe: ${editingRecipe?.title ?: "null"}")
        Log.d(TAG, "OCR recipe data: ${ocrRecipeData ?: "null"}")

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            // Handle UI state changes
            LaunchedEffect(uiState) {
                when (val state = uiState) {
                    is RecipeViewModel.RecipeUiState.Success -> {
                        Log.d(TAG, "Recipe saved successfully: ${state.message}")
                        Toast.makeText(this@AddEditRecipeActivity, state.message, Toast.LENGTH_SHORT).show()
                        
                        // Show interstitial ad when recipe is saved successfully
                        AdUtils.showInterstitialAd(this@AddEditRecipeActivity)
                        
                        finish()
                    }
                    is RecipeViewModel.RecipeUiState.Error -> {
                        Log.e(TAG, "Error saving recipe: ${state.message}")
                        Toast.makeText(this@AddEditRecipeActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }

            PaleoRecipesTheme {
                // If we have OCR data, parse it and create a recipe
                val recipeFromOcr = ocrRecipeData?.let { parseOcrRecipeData(it) }

                // Determine if we're editing an existing recipe or creating a new one from OCR
                val recipeToEdit = editingRecipe ?: recipeFromOcr

                // Show a message if we're loading a recipe from OCR
                if (recipeFromOcr != null && editingRecipe == null) {
                    Toast.makeText(this@AddEditRecipeActivity, "Recipe loaded from OCR scan. Please review, edit the title, and add ingredients/instructions as needed.", Toast.LENGTH_LONG).show()
                }

                AddEditRecipeScreen(
                    recipe = recipeToEdit,
                    onSaveRecipe = { recipe ->
                        Log.d(TAG, "Saving recipe: ${recipe.title}")
                        if (editingRecipe != null) {
                            viewModel.updateRecipe(recipe)
                        } else {
                            viewModel.insertRecipe(recipe)
                        }
                    },
                    onNavigateBack = {
                        Log.d(TAG, "Navigate back pressed")
                        finish()
                    },
                )
            }
        }
    }

    private fun parseOcrRecipeData(ocrData: String): Recipe? {
        return try {
            Log.d(TAG, "Parsing OCR data: $ocrData")
            val json = JSONObject(ocrData)
            Recipe(
                id = 0L,
                title = json.optString("title", "Scanned Recipe - Please Edit Title"),
                description = json.optString("description", "Recipe scanned using OCR. Please edit this field with your recipe details."),
                category = json.optString("category", "Scanned"),
                prepTime = json.optInt("prepTime", 0),
                cookTime = json.optInt("cookTime", 0),
                servings = json.optInt("servings", 1),
                notes = "",
                ingredients = json.optJSONArray("ingredients")?.let { array ->
                    (0 until array.length()).map {
                        try {
                            array.getString(it)
                        } catch (e: Exception) {
                            Log.w(TAG, "Error getting ingredient at index $it: ${e.message}")
                            ""
                        }
                    }.filter { it.isNotBlank() }.toMutableList()
                } ?: mutableListOf(),
                instructions = json.optJSONArray("instructions")?.let { array ->
                    (0 until array.length()).map {
                        try {
                            array.getString(it)
                        } catch (e: Exception) {
                            Log.w(TAG, "Error getting instruction at index $it: ${e.message}")
                            ""
                        }
                    }.filter { it.isNotBlank() }.toMutableList()
                } ?: mutableListOf(),
                isUserCreated = true,
                isFavorite = false,
                dateAdded = System.currentTimeMillis(),
            ).also { recipe ->
                Log.d(TAG, "Created recipe from OCR: ${recipe.title}")
                Log.d(TAG, "Description length: ${recipe.description.length}")
                Log.d(TAG, "Ingredients count: ${recipe.ingredients.size}")
                Log.d(TAG, "Instructions count: ${recipe.instructions.size}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing OCR data: ${e.message}", e)
            Toast.makeText(this, "Error processing scanned recipe data: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }
}