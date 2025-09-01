package com.leo.paleorecipes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.databinding.ActivityRecipeListBinding
import com.leo.paleorecipes.viewmodel.RecipeViewModel

class RecipeListActivity : AppCompatActivity() {

    private val TAG = "RecipeListActivity"
    private lateinit var binding: ActivityRecipeListBinding
    private lateinit var viewModel: RecipeViewModel
    private lateinit var adapter: RecipeAdapter
    private var isUserRecipes = false
    private var editMode = false
    private var printMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Log the start of onCreate
        Log.d(TAG, "onCreate started")

        try {
            // Initialize binding
            binding = ActivityRecipeListBinding.inflate(layoutInflater)
            setContentView(binding.root)

            Log.d(TAG, "Binding initialized and content view set")

            // Enable back button in action bar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Get intent extras
            isUserRecipes = intent.getBooleanExtra("isUserRecipes", false)
            editMode = intent.getBooleanExtra("editMode", false)
            printMode = intent.getBooleanExtra("printMode", false)

            Log.d(TAG, "Intent extras: isUserRecipes=$isUserRecipes, editMode=$editMode, printMode=$printMode")

            // Set title in the header TextView using localized strings
            val headerTitle = when {
                isUserRecipes && editMode -> getString(R.string.edit_your_recipes)
                isUserRecipes && printMode -> getString(R.string.select_recipe_to_print)
                isUserRecipes -> getString(R.string.your_recipes)
                else -> getString(R.string.paleo_recipes)
            }
            binding.textViewHeader.text = headerTitle
            supportActionBar?.title = headerTitle

            // If in print mode, show a toast with instructions
            if (printMode) {
                Toast.makeText(this, getString(R.string.tap_to_print), Toast.LENGTH_LONG).show()
            }

            // Initialize ViewModel
            viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

            // Setup RecyclerView
            setupRecyclerView()

            // Setup search
            setupSearch()

            // Observe data
            observeViewModel()

            // Setup FAB
            setupFab()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        try {
            adapter = RecipeAdapter(
                editMode = editMode,
                printMode = printMode,
                onRecipeClick = { recipe ->
                    if (printMode) {
                        printRecipe(recipe)
                    } else {
                        val intent = Intent(this, RecipeDetailActivity::class.java)
                        intent.putExtra("recipe", recipe)
                        startActivity(intent)
                    }
                },
                onEditClick = { recipe ->
                    val intent = Intent(this, AddEditRecipeActivity::class.java)
                    intent.putExtra("recipe", recipe)
                    startActivity(intent)
                },
                onDeleteClick = { recipe ->
                    showDeleteConfirmationDialog(recipe)
                },
                onPrintClick = { recipe ->
                    printRecipe(recipe)
                }
            )

            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
            Log.d(TAG, "RecyclerView setup complete")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView: ${e.message}", e)
        }
    }

    private fun setupSearch() {
        try {
            val searchEditText = findViewById<EditText>(R.id.editTextSearch)
            Log.d(TAG, "Search EditText found: ${searchEditText != null}")

            searchEditText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    Log.d(TAG, "Search query changed: $query")

                    if (query.isEmpty()) {
                        observeViewModel()
                    } else if (query.length >= 2) {
                        performSearch(query)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up search: ${e.message}", e)
        }
    }

    private fun setupFab() {
        try {
            if (binding.fab != null) {
                binding.fab.visibility = if (isUserRecipes && !editMode && !printMode) View.VISIBLE else View.GONE
                binding.fab.setOnClickListener {
                    val intent = Intent(this, AddEditRecipeActivity::class.java)
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up FAB: ${e.message}", e)
        }
    }

    private fun performSearch(query: String) {
        try {
            Log.d(TAG, "Performing search with query: $query")
            if (isUserRecipes) {
                viewModel.searchUserRecipes(query).observe(this) { recipes ->
                    adapter.submitList(recipes)
                    updateEmptyView(recipes.isEmpty())
                }
            } else {
                viewModel.searchPaleoRecipes(query).observe(this) { recipes ->
                    adapter.submitList(recipes)
                    updateEmptyView(recipes.isEmpty())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error performing search: ${e.message}", e)
        }
    }

    private fun observeViewModel() {
        try {
            Log.d(TAG, "Observing ViewModel data")
            if (isUserRecipes) {
                viewModel.allUserRecipes.observe(this) { recipes ->
                    adapter.submitList(recipes)
                    updateEmptyView(recipes.isEmpty())
                }
            }
            else {
                viewModel.allPaleoRecipes.observe(this) { recipes ->
                    adapter.submitList(recipes)
                    updateEmptyView(recipes.isEmpty())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error observing ViewModel: ${e.message}", e)
        }
    }

    private fun updateEmptyView(isEmpty: Boolean) {
        try {
            if (isEmpty) {
                binding.textViewEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE

                // Set appropriate empty message based on mode using localized strings
                val emptyMessage = when {
                    isUserRecipes && printMode -> getString(R.string.no_recipes_to_print)
                    isUserRecipes -> getString(R.string.no_user_recipes)
                    else -> getString(R.string.no_paleo_recipes)
                }
                binding.textViewEmpty.text = emptyMessage
            } else {
                binding.textViewEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating empty view: ${e.message}", e)
        }
    }

    private fun showDeleteConfirmationDialog(recipe: Recipe) {
        try {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_recipe))
                .setMessage(getString(R.string.delete_confirmation_message, recipe.title))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    Log.d(TAG, "Deleting recipe: ${recipe.title}")
                    viewModel.delete(recipe)
                    Toast.makeText(this, getString(R.string.recipe_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing delete dialog: ${e.message}", e)
        }
    }

    private fun printRecipe(recipe: Recipe) {
        try {
            Log.d(TAG, "Printing recipe: ${recipe.title}")

            // Create a print manager
            val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager

            // Create a print adapter
            val printAdapter = RecipePrintAdapter(this, recipe)

            // Create a print job name
            val jobName = "${recipe.title} Recipe"

            // Create a print job
            printManager.print(jobName, printAdapter, null)

            Toast.makeText(this, getString(R.string.preparing_to_print, recipe.title), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error printing recipe: ${e.message}", e)
            Toast.makeText(this, getString(R.string.error_printing, e.message), Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        // Refresh data when returning to this activity
        observeViewModel()
    }
}