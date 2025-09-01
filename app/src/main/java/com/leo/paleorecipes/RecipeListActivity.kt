package com.leo.paleorecipes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.print.PrintManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.databinding.ActivityRecipeListBinding
import com.leo.paleorecipes.utils.RecipeBackupManager
import com.leo.paleorecipes.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class RecipeListActivity : AppCompatActivity() {

    private val TAG = "RecipeListActivity"
    private lateinit var binding: ActivityRecipeListBinding
    private val viewModel: RecipeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter
    private var isUserRecipes = false
    private var editMode = false
    private var printMode = false

    @Inject
    lateinit var recipeBackupManager: RecipeBackupManager

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu called")
        menuInflater.inflate(R.menu.menu_main, menu)
        Log.d(TAG, "Menu inflated with ${menu.size()} items")
        // Log each menu item
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            Log.d(TAG, "Menu item $i: ${item.title} (ID: ${item.itemId})")
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected called with item ID: ${item.itemId}")
        when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "Home button pressed")
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            R.id.action_export_recipes -> {
                Log.d(TAG, "Export recipes menu item pressed")
                exportRecipes()
                return true
            }
            R.id.action_import_recipes -> {
                Log.d(TAG, "Import recipes menu item pressed")
                importRecipes()
                return true
            }
            R.id.action_about -> {
                Log.d(TAG, "About menu item pressed")
                startActivity(Intent(this, AboutPaleoActivity::class.java))
                return true
            }
            R.id.action_exit -> {
                Log.d(TAG, "Exit menu item pressed")
                finishAffinity()
                return true
            }
            R.id.action_backup_location -> {
                Log.d(TAG, "Show backup location menu item pressed")
                showBackupLocationInfo()
                return true
            }
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
                        try {
                            Log.d(TAG, "Starting RecipeDetailActivity with recipe: ${recipe.title}")
                            Log.d(TAG, "Recipe class: ${recipe.javaClass.name}")

                            val intent = Intent(this@RecipeListActivity, RecipeDetailActivity::class.java).apply {
                                // Ensure we're using the correct Recipe class
                                val recipeToPass = recipe as? com.leo.paleorecipes.data.Recipe
                                    ?: throw IllegalStateException("Incorrect Recipe class: ${recipe.javaClass.name}")

                                putExtra("recipe", recipeToPass)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                Log.d(TAG, "Intent extras: ${this.extras}")
                            }

                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error starting RecipeDetailActivity: ${e.message}", e)
                            runOnUiThread {
                                Toast.makeText(
                                    this@RecipeListActivity,
                                    "Error opening recipe: ${e.message}",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }
                    }
                },
                onEditClick = { recipe ->
                    val intent = Intent(this, AddEditRecipeActivity::class.java).apply {
                        putExtra("recipe", recipe as com.leo.paleorecipes.data.Recipe)
                    }
                    startActivity(intent)
                },
                onDeleteClick = { recipe ->
                    showDeleteConfirmationDialog(recipe as com.leo.paleorecipes.data.Recipe)
                },
                onPrintClick = { recipe ->
                    printRecipe(recipe as com.leo.paleorecipes.data.Recipe)
                },
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
            val searchEditText = binding.editTextSearch
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
                viewModel.searchUserRecipes(query)
                // Observe the search results LiveData
                viewModel.searchResults.observe(this) { recipes ->
                    adapter.submitList(recipes)
                    updateEmptyView(recipes.isEmpty())
                }
            } else {
                // For paleo recipes, search by ingredients
                viewModel.searchRecipesByIngredients(listOf(query))
                // Observe the search results LiveData
                viewModel.searchResults.observe(this) { recipes ->
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
            // For now, we'll use allUserRecipes for both cases since that's what's available
            // You might want to add allPaleoRecipes to the ViewModel if needed
            viewModel.allUserRecipes.observe(this) { recipes ->
                // Filter recipes based on isUserRecipes flag if needed
                val filteredRecipes = if (!isUserRecipes) {
                    // If we're showing paleo recipes, filter out user-created ones
                    recipes.filter { !it.isUserCreated }
                } else {
                    recipes
                }
                adapter.submitList(filteredRecipes)
                updateEmptyView(filteredRecipes.isEmpty())
            }

            // If you need to load paleo recipes from an API, you can call:
            if (!isUserRecipes) {
                viewModel.searchRecipesByIngredients(listOf("paleo", "healthy"))
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

    private fun showDeleteConfirmationDialog(recipe: com.leo.paleorecipes.data.Recipe) {
        try {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_recipe))
                .setMessage(getString(R.string.delete_confirmation_message, recipe.title))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    Log.d(TAG, "Deleting recipe: ${recipe.title}")
                    viewModel.deleteRecipe(recipe)
                    Toast.makeText(this, getString(R.string.recipe_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing delete dialog: ${e.message}", e)
        }
    }

    private fun printRecipe(recipe: com.leo.paleorecipes.data.Recipe) {
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

    private fun exportRecipes() {
        try {
            Log.d(TAG, "Exporting recipes")
            // Get all user recipes from the database
            val recipes = viewModel.allUserRecipes.value
            
            if (recipes != null) {
                // Filter to only user-created recipes
                val userRecipes = recipes.filter { it.isUserCreated }
                Log.d(TAG, "Found ${userRecipes.size} user recipes to export")
                
                if (userRecipes.isEmpty()) {
                    Toast.makeText(
                        this,
                        "No user-created recipes found to export. Please add some recipes first.",
                        Toast.LENGTH_LONG,
                    ).show()
                    return
                }

                // Export the recipes
                val result = recipeBackupManager.exportRecipes(userRecipes)

                if (result.isSuccess) {
                    val filePath = result.getOrNull()
                    Log.d(TAG, "Export successful: $filePath")
                    Toast.makeText(
                        this,
                        "Recipes exported successfully!\n\nFile saved to:\n$filePath\n\n" +
                        "You can find this file in your device's file manager.\n" +
                        "Look for files named: paleo_recipes_backup_*.json",
                        Toast.LENGTH_LONG,
                    ).show()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    Log.e(TAG, "Export failed: $error")
                    Toast.makeText(
                        this,
                        "Export failed: $error",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else {
                // If recipes are null, try to load them first
                Log.w(TAG, "Recipes data not available, attempting to load...")
                Toast.makeText(
                    this,
                    "Loading recipes for export...",
                    Toast.LENGTH_SHORT,
                ).show()
                
                // Load recipes and then export
                viewModel.loadAllUserRecipes()
                
                // Since loadAllUserRecipes is async, we need to observe the result
                viewModel.allUserRecipes.observe(this) { loadedRecipes ->
                    // Filter to only user-created recipes
                    val userRecipes = loadedRecipes.filter { it.isUserCreated }
                    Log.d(TAG, "Found ${userRecipes.size} user recipes to export after loading")
                    
                    if (userRecipes.isEmpty()) {
                        Toast.makeText(
                            this,
                            "No user-created recipes found to export. Please add some recipes first.",
                            Toast.LENGTH_LONG,
                        ).show()
                        
                        // Remove observer after one use to prevent multiple exports
                        viewModel.allUserRecipes.removeObservers(this)
                        return@observe
                    }

                    // Export the recipes
                    val result = recipeBackupManager.exportRecipes(userRecipes)

                    if (result.isSuccess) {
                        val filePath = result.getOrNull()
                        Log.d(TAG, "Export successful: $filePath")
                        Toast.makeText(
                            this,
                            "Recipes exported successfully!\n\nFile saved to:\n$filePath\n\n" +
                            "You can find this file in your device's file manager.\n" +
                            "Look for files named: paleo_recipes_backup_*.json",
                            Toast.LENGTH_LONG,
                        ).show()
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Unknown error"
                        Log.e(TAG, "Export failed: $error")
                        Toast.makeText(
                            this,
                            "Export failed: $error",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                    
                    // Remove observer after one use to prevent multiple exports
                    viewModel.allUserRecipes.removeObservers(this)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting recipes", e)
            Toast.makeText(
                this,
                "Error exporting recipes: ${e.message}",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun importRecipes() {
        try {
            Log.d(TAG, "Importing recipes")
            // Launch file picker to select backup file
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/json"
                addCategory(Intent.CATEGORY_OPENABLE)
                // Add extra MIME types to make JSON files more visible
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain", "application/octet-stream"))
                // Try to direct to the Downloads/PaleoRecipes folder
                val downloadsPath = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                val paleoRecipesPath = java.io.File(downloadsPath, "PaleoRecipes")
                // Note: We can't directly set the initial directory for ACTION_GET_CONTENT,
                // but we can provide a hint to the user about where to look
            }

            importFileLauncher.launch(Intent.createChooser(intent, "Select backup file (.json) - Look in Downloads/PaleoRecipes/"))
        } catch (e: Exception) {
            Log.e(TAG, "Error importing recipes", e)
            Toast.makeText(
                this,
                "Error importing recipes: ${e.message}",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    /**
     * Shows a toast with information about where backup files are stored
     */
    private fun showBackupLocationInfo() {
        recipeBackupManager.showBackupLocationInfo(this)
    }

    // Activity result launcher for file selection
    private val importFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        try {
            Log.d(TAG, "Import file launcher result: ${result.resultCode}")
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                Log.d(TAG, "Import file URI: $uri")
                if (uri != null) {
                    // Show loading message
                    Toast.makeText(this, "Importing recipes...", Toast.LENGTH_SHORT).show()
                    
                    // Import the recipes
                    val importResult = recipeBackupManager.importRecipes(uri)

                    if (importResult.isSuccess) {
                        val recipes = importResult.getOrNull() ?: emptyList()
                        Log.d(TAG, "Import successful, ${recipes.size} recipes imported")

                        // Save imported recipes to database
                        if (recipes.isNotEmpty()) {
                            viewModel.insertRecipes(recipes)

                            Toast.makeText(
                                this,
                                "Import successful!\n\nSuccessfully imported ${recipes.size} recipes.",
                                Toast.LENGTH_LONG,
                            ).show()
                            
                            // Refresh the recipe list
                            observeViewModel()
                        } else {
                            Toast.makeText(
                                this,
                                "No recipes found in the selected file. Please make sure you're selecting a valid Paleo Recipes backup file.",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    } else {
                        val error = importResult.exceptionOrNull()?.message ?: "Unknown error"
                        Log.e(TAG, "Import failed: $error")
                        Toast.makeText(
                            this,
                            "Import failed: $error",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                } else {
                    Log.w(TAG, "No file selected for import")
                    Toast.makeText(
                        this,
                        "No file selected. Please select a JSON backup file.\n\n" +
                        "Look for files named: paleo_recipes_backup_*.json\n" +
                        "These files are typically located in your Downloads/PaleoRecipes folder.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            } else if (result.resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Import file selection cancelled")
                Toast.makeText(
                    this,
                    "Import cancelled",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                Log.e(TAG, "Import file selection failed with result code: ${result.resultCode}")
                Toast.makeText(
                    this,
                    "Import failed. Please try again.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling imported file", e)
            Toast.makeText(
                this,
                "Error importing file: ${e.message}",
                Toast.LENGTH_LONG,
            ).show()
        }
    }
}
