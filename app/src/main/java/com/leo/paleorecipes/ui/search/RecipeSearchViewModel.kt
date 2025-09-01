package com.leo.paleorecipes.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeSearchViewModel @Inject constructor(
    private val repository: RecipeRepository,
) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var searchJob: Job? = null

    fun searchRecipes(ingredients: List<String>) {
        // Cancel any previous search
        searchJob?.cancel()

        if (ingredients.isEmpty()) {
            _recipes.value = emptyList()
            _error.value = "Please add some ingredients to search"
            return
        }

        _isLoading.value = true
        _error.value = null

        searchJob = viewModelScope.launch {
            try {
                val result = repository.searchRecipesByIngredients(ingredients)
                _recipes.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred while searching recipes"
                _recipes.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
                repository.updateRecipe(updatedRecipe)
                // Update the list with the updated recipe
                _recipes.value = _recipes.value?.map {
                    if (it.id == updatedRecipe.id) updatedRecipe else it
                }
            } catch (e: Exception) {
                _error.value = "Failed to update favorite status: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}
