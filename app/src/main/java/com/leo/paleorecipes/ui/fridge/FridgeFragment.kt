package com.leo.paleorecipes.ui.fridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.leo.paleorecipes.adapter.IngredientsAdapter
import com.leo.paleorecipes.databinding.FragmentFridgeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FridgeFragment : Fragment() {
    private var _binding: FragmentFridgeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FridgeViewModel by viewModels()
    private lateinit var adapter: IngredientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFridgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = IngredientsAdapter(
            onRemoveClick = { ingredient ->
                viewModel.removeIngredient(ingredient)
            },
        )
        binding.ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FridgeFragment.adapter
        }
    }

    private fun setupClickListeners() {
        binding.addButton.setOnClickListener {
            val ingredient = binding.ingredientInput.text.toString().trim()
            if (ingredient.isNotBlank()) {
                viewModel.addIngredient(ingredient)
                binding.ingredientInput.text?.clear()
            }
        }

        binding.ingredientInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val ingredient = binding.ingredientInput.text.toString().trim()
                if (ingredient.isNotBlank()) {
                    viewModel.addIngredient(ingredient)
                    binding.ingredientInput.text?.clear()
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun observeViewModel() {
        viewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            adapter.submitList(ingredients)
        }

        viewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
