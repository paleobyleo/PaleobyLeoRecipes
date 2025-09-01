package com.example.paleorecipes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.paleorecipes.data.Recipe
import com.example.paleorecipes.databinding.ActivityAddEditRecipeBinding
import com.example.paleorecipes.viewmodel.RecipeViewModel

class AddEditRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditRecipeBinding
    private lateinit var recipeViewModel: RecipeViewModel
    private var recipe: Recipe? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        // Check if we're editing an existing recipe
        if (intent.hasExtra("recipe")) {
            recipe = intent.getParcelableExtra("recipe")
            isEditMode = true
            title = "Edit Recipe"
            populateFields()
        } else {
            title = "Add New Recipe"
        }

        binding.buttonSave.setOnClickListener {
            saveRecipe()
        }
    }

    private fun populateFields() {
        recipe?.let {
            binding.editTextTitle.setText(it.title)
            binding.editTextIngredients.setText(it.ingredients)
            binding.editTextInstructions.setText(it.instructions)
            binding.editTextImageUrl.setText(it.imageUrl)
            binding.editTextSourceUrl.setText(it.sourceUrl)
        }
    }

    private fun saveRecipe() {
        val title = binding.editTextTitle.text.toString().trim()
        val ingredients = binding.editTextIngredients.text.toString().trim()
        val instructions = binding.editTextInstructions.text.toString().trim()
        val imageUrl = binding.editTextImageUrl.text.toString().trim()
        val sourceUrl = binding.editTextSourceUrl.text.toString().trim()

        if (title.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (isEditMode) {
            recipe?.let {
                val updatedRecipe = it.copy(
                    title = title,
                    ingredients = ingredients,
                    instructions = instructions,
                    imageUrl = imageUrl,
                    sourceUrl = sourceUrl
                )
                recipeViewModel.update(updatedRecipe)
                Toast.makeText(this, "Recipe updated", Toast.LENGTH_SHORT).show()
            }
        } else {
            val newRecipe = Recipe(
                title = title,
                ingredients = ingredients,
                instructions = instructions,
                imageUrl = imageUrl,
                sourceUrl = sourceUrl,
                isUserCreated = true
            )
            recipeViewModel.insert(newRecipe)
            Toast.makeText(this, "Recipe added", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}