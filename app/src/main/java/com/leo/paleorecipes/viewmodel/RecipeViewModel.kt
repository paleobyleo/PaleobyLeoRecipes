package com.leo.paleorecipes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for managing recipe data and UI state.
 * Handles all business logic related to recipes and exposes data to the UI.
 *
 * @property application The application context.
 * @property repository The repository for recipe data operations.
 */
@HiltViewModel
class RecipeViewModel @Inject constructor(
    application: Application,
    private val repository: RecipeRepository,
) : AndroidViewModel(application) {

    // Flow for all user recipes
    private val _allUserRecipes = MutableLiveData<List<Recipe>>()
    val allUserRecipes: LiveData<List<Recipe>> = _allUserRecipes

    // StateFlow for UI state
    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState

    // For recipe details
    private val _selectedRecipe = MutableLiveData<Recipe>()
    val selectedRecipe: LiveData<Recipe> = _selectedRecipe

    // For search results
    private val _searchResults = MutableLiveData<List<Recipe>>()
    val searchResults: LiveData<List<Recipe>> = _searchResults

    // Current search query
    private var currentSearchQuery = ""

    // Active search job to cancel previous search
    private var searchJob: kotlinx.coroutines.Job? = null

    /**
     * Insert a new recipe into the database.
     *
     * @param recipe The recipe to insert.
     */
    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val insertedRecipe = withContext(Dispatchers.IO) { repository.insertRecipe(recipe) }
                _uiState.value = RecipeUiState.Success("Recipe added successfully")
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error("Failed to add recipe: ${e.message}")
            }
        }
    }

    /**
     * Insert multiple recipes into the database.
     *
     * @param recipes The list of recipes to insert.
     */
    fun insertRecipes(recipes: List<Recipe>) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                withContext(Dispatchers.IO) {
                    // Insert all recipes
                    repository.insertRecipes(recipes)
                    // Reload all user recipes to reflect the changes
                    loadAllUserRecipes()
                }
                _uiState.value = RecipeUiState.Success("Recipes imported successfully")
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error("Failed to import recipes: ${e.message}")
            }
        }
    }

    /**
     * Update an existing recipe in the database.
     *
     * @param recipe The recipe to update.
     */
    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                withContext(Dispatchers.IO) { repository.updateRecipe(recipe) }
                _uiState.value = RecipeUiState.Success("Recipe updated successfully")
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error("Failed to update recipe: ${e.message}")
            }
        }
    }

    /**
     * Delete a recipe from the database.
     *
     * @param recipe The recipe to delete.
     */
    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                withContext(Dispatchers.IO) {
                    repository.deleteRecipe(recipe.id)
                    // Reload recipes after deletion
                    val currentRecipes = _allUserRecipes.value?.toMutableList()
                    currentRecipes?.removeAll { it.id == recipe.id }
                    _allUserRecipes.postValue(currentRecipes ?: emptyList())
                }
                _uiState.value = RecipeUiState.Success("Recipe deleted successfully")
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error("Failed to delete recipe: ${e.message}")
            }
        }
    }

    /**
     * Toggle the favorite status of a recipe.
     *
     * @param recipe The recipe to toggle favorite status for.
     */
    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
            updateRecipe(updatedRecipe)
        }
    }

    /**
     * Load all user recipes from the repository.
     * This method observes the repository's user recipes flow and updates the LiveData.
     */
    fun loadAllUserRecipes() {
        viewModelScope.launch {
            try {
                repository.getAllUserRecipes()
                    .collectLatest { recipes ->
                        _allUserRecipes.value = recipes
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading user recipes")
                _allUserRecipes.value = emptyList()
            }
        }
    }

    /**
     * Set the selected recipe for viewing details.
     *
     * @param recipe The recipe to select.
     */
    fun setSelectedRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
    }

    /**
     * Load a recipe by ID.
     *
     * @param id The ID of the recipe to load.
     */
    fun loadRecipeById(id: Long) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipe = withContext(Dispatchers.IO) { repository.getRecipeById(id) }
                recipe?.let {
                    _selectedRecipe.value = it
                    _uiState.value = RecipeUiState.RecipeLoaded(it)
                } ?: run {
                    _uiState.value = RecipeUiState.Error("Recipe not found")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeUiState.Error("Failed to load recipe: ${e.message}")
            }
        }
    }

    /**
     * Search for recipes by ingredients.
     *
     * @param ingredients The list of ingredients to search for.
     */
    fun searchRecipesByIngredients(ingredients: List<String>) {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                val recipes = withContext(Dispatchers.IO) {
                    repository.searchRecipesByIngredients(ingredients)
                }
                _searchResults.value = recipes
                _uiState.value = if (recipes.isEmpty()) {
                    RecipeUiState.Empty("No recipes found with these ingredients")
                } else {
                    RecipeUiState.Success("Found ${recipes.size} recipes")
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _uiState.value = RecipeUiState.Error("Failed to search recipes: ${e.message}")
            }
        }
    }

    /**
     * Search user recipes by title.
     *
     * @param query The search query.
     */
    fun searchUserRecipes(query: String) {
        currentSearchQuery = query.trim()

        // Cancel previous search if any
        searchJob?.cancel()

        if (currentSearchQuery.isBlank()) {
            _searchResults.value = emptyList()
            _uiState.value = RecipeUiState.Empty("Please enter a search term")
            return
        }

        _uiState.value = RecipeUiState.Loading

        searchJob = viewModelScope.launch {
            try {
                repository.searchUserRecipes(currentSearchQuery)
                    .collectLatest { recipes ->
                        _searchResults.value = recipes
                        _uiState.value = if (recipes.isEmpty()) {
                            RecipeUiState.Empty("No recipes found for '$currentSearchQuery'")
                        } else {
                            RecipeUiState.Success("Found ${recipes.size} recipes")
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error searching recipes")
                _searchResults.value = emptyList()
                _uiState.value = RecipeUiState.Error("Failed to search recipes: ${e.message}")
            }
        }
    }

    /**
     * Get favorite recipes.
     * Observes the favorite recipes from the repository and updates the UI state accordingly.
     */
    fun loadFavoriteRecipes() {
        viewModelScope.launch {
            _uiState.value = RecipeUiState.Loading
            try {
                repository.getFavoriteRecipes()
                    .collectLatest { favorites ->
                        _searchResults.value = favorites
                        _uiState.value = if (favorites.isEmpty()) {
                            RecipeUiState.Empty("No favorite recipes yet")
                        } else {
                            RecipeUiState.Success("Found ${favorites.size} favorites")
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error loading favorite recipes")
                _searchResults.value = emptyList()
                _uiState.value = RecipeUiState.Error("Failed to load favorites: ${e.message}")
            }
        }
    }

    /**
     * Get current search query
     */
    fun getCurrentSearchQuery(): String {
        return currentSearchQuery
    }

    /**
     * Clear the search results
     */
    fun clearSearchResults() {
        _searchResults.value = emptyList()
        currentSearchQuery = ""
    }

    /**
     * Get a recipe by ID
     * @param id The ID of the recipe to retrieve
     * @return Flow containing the recipe or null if not found
     */
    fun getRecipeById(id: Long): Flow<Recipe?> {
        return repository.observeRecipeById(id)
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel any ongoing coroutines when ViewModel is cleared
        searchJob?.cancel()
    }

    /**
     * Sealed class representing different UI states for the Recipe screen.
     */
    sealed class RecipeUiState {
        /**
         * Represents the loading state.
         */
        object Loading : RecipeUiState()

        /**
         * Represents the success state with a success message.
         * @property message The success message to display.
         */
        data class Success(val message: String) : RecipeUiState()

        /**
         * Represents the error state with an error message.
         * @property message The error message to display.
         */
        data class Error(val message: String) : RecipeUiState()

        /**
         * Represents the empty state with a message.
         * @property message The message to display when no data is available.
         */
        data class Empty(val message: String) : RecipeUiState()

        /**
         * Represents the state when a recipe is loaded.
         * @property recipe The loaded recipe.
         */
        data class RecipeLoaded(val recipe: Recipe) : RecipeUiState()
    }
}
