package com.example.paleorecipes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paleorecipes.data.Recipe
import com.example.paleorecipes.databinding.ActivityRecipeListBinding
import com.example.paleorecipes.viewmodel.RecipeViewModel

class RecipeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeListBinding
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter
    private var isUserRecipes = false
    private var editMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isUserRecipes = intent.getBooleanExtra("isUserRecipes", false)
        editMode = intent.getBooleanExtra("editMode", false)

        title = if (isUserRecipes) {
            if (editMode) "Edit Your Recipes" else "Your Recipes"
        } else {
            "Paleo Recipes"
        }

        recipeViewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

        adapter = RecipeAdapter(
            editMode = editMode,
            onRecipeClick = { recipe ->
                val intent = Intent(this, RecipeDetailActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
            },
            onEditClick = { recipe ->
                val intent = Intent(this, AddEditRecipeActivity::class.java)
                intent.putExtra("recipe", recipe)
                startActivity(intent)
            },
            onDeleteClick = { recipe ->
                showDeleteConfirmationDialog(recipe)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        observeRecipes()
    }

    private fun observeRecipes() {
        val recipesLiveData = if (isUserRecipes) {
            recipeViewModel.allUserRecipes
        } else {
            recipeViewModel.allPaleoRecipes
        }

        recipesLiveData.observe(this) { recipes ->
            if (recipes.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.submitList(recipes)
            }
        }
    }

    private fun showDeleteConfirmationDialog(recipe: Recipe) {
        AlertDialog.Builder(this)
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete ${recipe.title}?")
            .setPositiveButton("Delete") { _, _ ->
                recipeViewModel.delete(recipe)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}