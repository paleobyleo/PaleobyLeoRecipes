package com.leo.paleorecipes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.databinding.ItemRecipeBinding

class RecipeAdapter(
    private val editMode: Boolean = false,
    private val printMode: Boolean = false,
    private val onRecipeClick: (Recipe) -> Unit,
    private val onEditClick: ((Recipe) -> Unit)? = null,
    private val onDeleteClick: ((Recipe) -> Unit)? = null,
    private val onPrintClick: ((Recipe) -> Unit)? = null,
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    private val TAG = "RecipeAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        Log.d(TAG, "Creating new ViewHolder")
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        try {
            val recipe = getItem(position)
            Log.d(TAG, "Binding ViewHolder for recipe: ${recipe.title}")
            holder.bind(recipe)
        } catch (e: Exception) {
            Log.e(TAG, "Error binding recipe at position $position: ${e.message}", e)
        }
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            try {
                // Debug logging
                Log.d("RecipeAdapter", "Binding recipe: ${recipe.title}")
                Log.d("RecipeAdapter", "Image URL: ${recipe.imageUrl}")

                // Log view hierarchy
                binding.root.let { root ->
                    Log.d("RecipeAdapter", "Root view: ${root.javaClass.simpleName} (${root.width}x${root.height})")
                    root.findViewById<View>(R.id.textViewPrepTime)?.let {
                        Log.d("RecipeAdapter", "Prep time view bounds: ${it.left},${it.top} - ${it.right},${it.bottom}")
                    }
                }

                // Set recipe title with null safety
                binding.textViewTitle.text = recipe.title ?: "Untitled Recipe"

                // Set recipe description (if available)
                if (!recipe.description.isNullOrEmpty()) {
                    binding.textViewDescription.text = recipe.description
                    binding.textViewDescription.visibility = View.VISIBLE
                } else {
                    binding.textViewDescription.visibility = View.GONE
                }

                // Load recipe image (if available) with proper scaling
                if (!recipe.imageUrl.isNullOrEmpty()) {
                    try {
                        binding.imageViewRecipe.load(recipe.imageUrl) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                            error(android.R.drawable.ic_menu_report_image)
                            scale(Scale.FILL) // This replaces CENTER_CROP functionality
                            transformations(RoundedCornersTransformation(16f))
                        }
                        binding.imageViewRecipe.visibility = View.VISIBLE
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading image for recipe: ${recipe.title}", e)
                        binding.imageViewRecipe.visibility = View.GONE
                    }
                } else {
                    binding.imageViewRecipe.visibility = View.GONE
                }

                // Set recipe info
                binding.textViewPrepTime.text = "${recipe.prepTime} min"
                binding.textViewCookTime.text = "${recipe.cookTime} min"
                binding.textViewServings.text = recipe.servings.toString()

                // Show edit/delete buttons in edit mode
                if (editMode && recipe.isUserCreated) {
                    binding.buttonEdit.visibility = View.VISIBLE
                    binding.buttonDelete.visibility = View.VISIBLE

                    binding.buttonEdit.setOnClickListener {
                        Log.d(TAG, "Edit button clicked for recipe: ${recipe.title}")
                        onEditClick?.invoke(recipe)
                    }

                    binding.buttonDelete.setOnClickListener {
                        Log.d(TAG, "Delete button clicked for recipe: ${recipe.title}")
                        onDeleteClick?.invoke(recipe)
                    }
                } else {
                    binding.buttonEdit.visibility = View.GONE
                    binding.buttonDelete.visibility = View.GONE
                }

                // In print mode, clicking the item prints the recipe
                // In normal mode, clicking the item opens recipe details
                binding.root.setOnClickListener {
                    Log.d(TAG, "Recipe item clicked: ${recipe.title}")
                    if (printMode) {
                        onPrintClick?.invoke(recipe)
                    } else {
                        onRecipeClick(recipe)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error binding recipe: ${e.message}", e)
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
