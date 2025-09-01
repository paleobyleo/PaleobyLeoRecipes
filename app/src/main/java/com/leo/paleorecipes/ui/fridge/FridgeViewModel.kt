package com.leo.paleorecipes.ui.fridge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leo.paleorecipes.data.repository.IngredientRepository
import com.leo.paleorecipes.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FridgeViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository,
) : ViewModel() {

    private val _ingredients = MutableLiveData<List<String>>()
    val ingredients: LiveData<List<String>> = _ingredients

    private val _snackbarMessage = SingleLiveEvent<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    init {
        loadIngredients()
    }

    fun addIngredient(ingredient: String) {
        viewModelScope.launch {
            try {
                ingredientRepository.addIngredient(ingredient)
                _snackbarMessage.value = "Ingredient added"
                loadIngredients()
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed to add ingredient: ${e.message}"
            }
        }
    }

    fun removeIngredient(ingredient: String) {
        viewModelScope.launch {
            try {
                ingredientRepository.removeIngredient(ingredient)
                _snackbarMessage.value = "Ingredient removed"
                loadIngredients()
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed to remove ingredient: ${e.message}"
            }
        }
    }

    fun onSnackbarShown() {
        _snackbarMessage.value = null
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            try {
                ingredientRepository.getIngredients().collect { ingredients ->
                    _ingredients.value = ingredients
                }
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed to load ingredients: ${e.message}"
            }
        }
    }
}
