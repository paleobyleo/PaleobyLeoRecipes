package com.leo.paleorecipes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.data.RecipeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository

    // LiveData for all recipes
    val allPaleoRecipes: LiveData<List<Recipe>>
    val allUserRecipes: LiveData<List<Recipe>>

    // For recipe details
    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> = _selectedRecipe

    // For search results
    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    // Current search query
    private var currentSearchQuery = ""

    init {
        // Initialize repository with application context
        repository = RecipeRepository(application)

        allPaleoRecipes = repository.allPaleoRecipes
        allUserRecipes = repository.allUserRecipes
    }

    /**
     * Insert a new recipe into the database
     */
    fun insert(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(recipe)
    }

    /**
     * Update an existing recipe in the database
     */
    fun update(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(recipe)
    }

    /**
     * Delete a recipe from the database
     */
    fun delete(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(recipe)
    }

    /**
     * Toggle the favorite status of a recipe
     */
    fun toggleFavorite(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
        repository.update(updatedRecipe)
    }

    /**
     * Set the selected recipe for viewing details
     */
    fun setSelectedRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
    }

    /**
     * Search user recipes by title
     */
    fun searchUserRecipes(query: String): LiveData<List<Recipe>> {
        currentSearchQuery = query
        return repository.searchUserRecipes(query)
    }

    /**
     * Search paleo recipes by title
     */
    fun searchPaleoRecipes(query: String): LiveData<List<Recipe>> {
        currentSearchQuery = query
        return repository.searchPaleoRecipes(query)
    }

    /**
     * Search all recipes by title and description
     */
    fun searchAllRecipes(query: String): LiveData<List<Recipe>> {
        currentSearchQuery = query
        return repository.searchAllRecipes(query)
    }

    /**
     * Get favorite recipes
     */
    fun getFavoriteRecipes(): LiveData<List<Recipe>> {
        return repository.getFavoriteRecipes()
    }

    /**
     * Get current search query
     */
    fun getCurrentSearchQuery(): String {
        return currentSearchQuery
    }

    /**
     * Clear search and return to all recipes
     */
    fun clearSearch() {
        currentSearchQuery = ""
    }

    /**
     * Get a recipe by ID
     */
    fun getRecipeById(id: Long): LiveData<Recipe> {
        return repository.getRecipeById(id)
    }
}