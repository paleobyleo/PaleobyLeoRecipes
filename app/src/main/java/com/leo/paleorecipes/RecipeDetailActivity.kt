package com.leo.paleorecipes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailBinding
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipe = intent.getParcelableExtra("recipe") ?: return

        setupUI()
    }

    private fun setupUI() {
        title = recipe.title

        binding.textViewTitle.text = recipe.title
        binding.textViewDescription.text = recipe.description

        // Display prep and cook time
        binding.textViewPrepTime.text = "Prep: ${recipe.prepTime} min"
        binding.textViewCookTime.text = "Cook: ${recipe.cookTime} min"
        binding.textViewServings.text = "Servings: ${recipe.servings}"

        // Display ingredients
        val ingredientsText = recipe.ingredients.joinToString("\n• ", "• ")
        binding.textViewIngredients.text = ingredientsText

        // Display instructions
        val instructionsText = recipe.instructions.mapIndexed { index, instruction ->
            "${index + 1}. $instruction"
        }.joinToString("\n\n")
        binding.textViewInstructions.text = instructionsText

        // Load image if available
        if (!recipe.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.imageViewRecipe)
        } else {
            binding.imageViewRecipe.setImageResource(R.drawable.placeholder_image)
        }
    }
}