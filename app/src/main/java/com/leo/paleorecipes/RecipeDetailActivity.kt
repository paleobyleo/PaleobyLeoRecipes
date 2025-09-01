package com.leo.paleorecipes

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.ui.recipe.RecipeDetailScreen
import com.leo.paleorecipes.ui.theme.PaleoRecipesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipe: Recipe
    private val TAG = "RecipeDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Safely get the Recipe from intent
            recipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("recipe", Recipe::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("recipe")
            } ?: throw IllegalStateException("No recipe data received")

            Log.d(TAG, "Successfully loaded recipe: ${recipe.title}")

            // Set up the Compose UI
            setContent {
                // Set solid black background for the entire app
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                ) {
                    PaleoRecipesTheme {
                        RecipeDetailScreen(
                            recipe = recipe,
                            onNavigateBack = { onBackPressedDispatcher.onBackPressed() },
                            onEditRecipe = {
                                val intent = android.content.Intent(this@RecipeDetailActivity, AddEditRecipeActivity::class.java).apply {
                                    putExtra("recipe", recipe)
                                }
                                startActivity(intent)
                            }
                        )
                    }
                }
            }

            // Enable back button in action bar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing activity: ${e.message}", e)
            Toast.makeText(this, "Error loading recipe: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}