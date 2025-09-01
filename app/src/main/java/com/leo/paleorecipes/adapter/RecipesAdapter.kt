package com.leo.paleorecipes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.leo.paleorecipes.R
import com.leo.paleorecipes.data.Recipe

class RecipesAdapter(
    private val onRecipeClick: (Recipe) -> Unit,
    private val onFavoriteClick: (Recipe) -> Unit,
) : ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val layoutId = when (viewType) {
            0 -> R.layout.item_recipe
            else -> R.layout.item_recipe_simple
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        // Return 0 for full recipe view, 1 for simple view
        return if (position < 4) 0 else 1
    }

    fun updateRecipe(updatedRecipe: Recipe) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.id == updatedRecipe.id }
        if (index != -1) {
            currentList[index] = updatedRecipe
            submitList(currentList)
        }
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.recipe_title)
        private val ingredientsText: TextView = itemView.findViewById(R.id.recipe_ingredients)
        private val timeText: TextView = itemView.findViewById(R.id.recipe_time)
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.favorite_icon)

        fun bind(recipe: Recipe) {
            titleText.text = recipe.title

            // Show first 3 ingredients or all if less than 3
            val ingredientsList = recipe.ingredients.take(3)
            val ingredientsTextValue = if (ingredientsList.size >= 3) {
                "${ingredientsList.joinToString(", ")}..."
            } else {
                ingredientsList.joinToString(", ")
            }
            ingredientsText.text = ingredientsTextValue

            // Format time (if available)
            timeText.text = recipe.prepTime.let { "$it min" }

            // Load image with Glide if URL is available
            recipe.imageUrl.takeIf { !it.isNullOrBlank() }?.let { imageUrl ->
                recipeImage.load(imageUrl) {
                    placeholder(R.drawable.placeholder_recipe)
                    error(R.drawable.placeholder_recipe)
                    scale(Scale.FILL)
                }
            } ?: recipeImage.setImageResource(R.drawable.placeholder_recipe)

            // Set favorite icon
            val favoriteRes = if (recipe.isFavorite) {
                R.drawable.ic_favorite_filled
            } else {
                R.drawable.ic_favorite_border
            }
            favoriteIcon.setImageResource(favoriteRes)

            // Set click listeners
            itemView.setOnClickListener { onRecipeClick(recipe) }
            favoriteIcon.setOnClickListener {
                onFavoriteClick(recipe.copy(isFavorite = !recipe.isFavorite))
            }
        }
    }
}

class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
}
