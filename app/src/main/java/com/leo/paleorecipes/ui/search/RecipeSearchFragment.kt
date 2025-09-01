package com.leo.paleorecipes.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.paleorecipes.adapter.RecipesAdapter
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.databinding.FragmentRecipeSearchListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeSearchFragment : Fragment() {

    private var _binding: FragmentRecipeSearchListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecipeSearchViewModel by viewModels()
    private lateinit var adapter: RecipesAdapter

    private val ingredients: List<String> by lazy {
        arguments?.getStringArrayList(ARG_INGREDIENTS)?.toList().orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecipeSearchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        if (ingredients.isNotEmpty()) {
            viewModel.searchRecipes(ingredients)
        } else {
            showEmptyState("No ingredients provided for search")
        }
    }

    private fun setupRecyclerView() {
        adapter = RecipesAdapter(
            onRecipeClick = { recipe ->
                // TODO: Navigate to recipe detail
                Toast.makeText(context, "Selected: ${recipe.title}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { recipe ->
                viewModel.toggleFavorite(recipe)
            },
        )

        binding.fragmentRecipesRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@RecipeSearchFragment.adapter
            this.setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNullOrEmpty()) {
                showEmptyState("No recipes found with these ingredients")
            } else {
                showRecipes(recipes)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.fragmentProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
            }
        }
    }

    private fun showRecipes(recipes: List<Recipe>) {
        binding.fragmentEmptyStateText.visibility = View.GONE
        binding.fragmentRecipesRecyclerView.visibility = View.VISIBLE
        adapter.submitList(recipes)
    }

    private fun showEmptyState(message: String) {
        binding.fragmentEmptyStateText.visibility = View.VISIBLE
        binding.fragmentRecipesRecyclerView.visibility = View.GONE
        binding.fragmentEmptyStateText.text = message
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_INGREDIENTS = "ingredients"

        fun newInstance(ingredients: List<String>): RecipeSearchFragment {
            return RecipeSearchFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_INGREDIENTS, ArrayList(ingredients))
                }
            }
        }
    }
}
