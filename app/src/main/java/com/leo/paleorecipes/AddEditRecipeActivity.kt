package com.leo.paleorecipes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.databinding.ActivityAddEditRecipeBinding
import com.leo.paleorecipes.viewmodel.RecipeViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AddEditRecipeActivity : AppCompatActivity() {
    private val TAG = "AddEditRecipeActivity"
    private lateinit var binding: ActivityAddEditRecipeBinding
    private lateinit var viewModel: RecipeViewModel
    private var editingRecipe: Recipe? = null
    private var selectedImageUri: Uri? = null

    // Register for activity result to handle image selection
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                displaySelectedImage(uri)
            }
        }
    }

    // Register for permission result
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if all required permissions are granted
        val allGranted = permissions.entries.all { it.value }

        if (allGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            Log.d(TAG, "onCreate: Starting AddEditRecipeActivity")

            // Initialize binding
            binding = ActivityAddEditRecipeBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Enable back button in action bar
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            // Initialize ViewModel
            viewModel = ViewModelProvider(this)[RecipeViewModel::class.java]

            // Check if we're editing an existing recipe
            if (intent.hasExtra("recipe")) {
                editingRecipe = intent.getParcelableExtra("recipe")
                editingRecipe?.let { populateFields(it) }
                title = "Edit Recipe"
            } else {
                title = "Add New Recipe"
            }

            // Set up save button
            binding.buttonSave.setOnClickListener {
                saveRecipe()
            }

            // Set up image selection button
            binding.buttonSelectImage.setOnClickListener {
                checkPermissionAndSelectImage()
            }
        } catch (e: Exception) {
            Log.e(TAG, "onCreate: Error initializing activity", e)
            Toast.makeText(this, "Error initializing: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkPermissionAndSelectImage() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+ (API 33+)
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request the permission
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
                } else {
                    // Permission already granted
                    openImagePicker()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6.0+ (API 23+) to Android 12
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request the permission
                    requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                } else {
                    // Permission already granted
                    openImagePicker()
                }
            } else {
                // For Android 5.1 and below, permissions are granted at install time
                openImagePicker()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking permissions: ${e.message}", e)
            Toast.makeText(this, "Error checking permissions: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagePicker() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error opening image picker: ${e.message}", e)
            Toast.makeText(this, "Error opening image picker: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displaySelectedImage(uri: Uri) {
        try {
            binding.imageViewPreview.visibility = View.VISIBLE
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(binding.imageViewPreview)
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying image: ${e.message}", e)
        }
    }

    private fun saveSelectedImageToAppStorage(): String? {
        selectedImageUri?.let { uri ->
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    val fileName = "recipe_image_${System.currentTimeMillis()}.jpg"
                    val file = File(filesDir, fileName)

                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(4 * 1024) // 4K buffer
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                        output.flush()
                    }

                    return "file://${file.absolutePath}"
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image: ${e.message}", e)
                Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return null
    }

    private fun populateFields(recipe: Recipe) {
        try {
            binding.editTextTitle.setText(recipe.title)
            binding.editTextDescription.setText(recipe.description)
            binding.editTextIngredients.setText(recipe.ingredients.joinToString("\n"))
            binding.editTextInstructions.setText(recipe.instructions.joinToString("\n"))
            binding.editTextPrepTime.setText(recipe.prepTime.toString())
            binding.editTextCookTime.setText(recipe.cookTime.toString())
            binding.editTextServings.setText(recipe.servings.toString())

            // Handle image URL (which is optional)
            if (!recipe.imageUrl.isNullOrEmpty()) {
                binding.editTextImageUrl.setText(recipe.imageUrl)

                // Load and display the image
                binding.imageViewPreview.visibility = View.VISIBLE
                Glide.with(this)
                    .load(recipe.imageUrl)
                    .centerCrop()
                    .into(binding.imageViewPreview)
            } else {
                binding.imageViewPreview.visibility = View.GONE
                binding.editTextImageUrl.setText("")
            }
        } catch (e: Exception) {
            Log.e(TAG, "populateFields: Error populating fields", e)
            Toast.makeText(this, "Error loading recipe data: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecipe() {
        try {
            val title = binding.editTextTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            val ingredientsText = binding.editTextIngredients.text.toString().trim()
            val instructionsText = binding.editTextInstructions.text.toString().trim()
            val prepTimeText = binding.editTextPrepTime.text.toString().trim()
            val cookTimeText = binding.editTextCookTime.text.toString().trim()
            val servingsText = binding.editTextServings.text.toString().trim()
            var imageUrl = binding.editTextImageUrl.text.toString().trim()

            // If a local image was selected, save it and use its path
            if (selectedImageUri != null) {
                val savedImagePath = saveSelectedImageToAppStorage()
                if (!savedImagePath.isNullOrEmpty()) {
                    imageUrl = savedImagePath
                }
            }

            // Validate required inputs
            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a recipe title", Toast.LENGTH_SHORT).show()
                return
            }

            if (ingredientsText.isEmpty()) {
                Toast.makeText(this, "Please enter ingredients", Toast.LENGTH_SHORT).show()
                return
            }

            if (instructionsText.isEmpty()) {
                Toast.makeText(this, "Please enter instructions", Toast.LENGTH_SHORT).show()
                return
            }

            // Parse numeric fields with defaults if empty
            val prepTime = if (prepTimeText.isEmpty()) 0 else prepTimeText.toInt()
            val cookTime = if (cookTimeText.isEmpty()) 0 else cookTimeText.toInt()
            val servings = if (servingsText.isEmpty()) 0 else servingsText.toInt()

            // Split ingredients and instructions by new line
            val ingredients = ingredientsText.split("\n").filter { it.isNotEmpty() }
            val instructions = instructionsText.split("\n").filter { it.isNotEmpty() }

            // Create or update recipe
            val recipe = if (editingRecipe != null) {
                Recipe(
                    id = editingRecipe!!.id,
                    title = title,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    prepTime = prepTime,
                    cookTime = cookTime,
                    servings = servings,
                    imageUrl = imageUrl,
                    isUserCreated = true
                )
            } else {
                Recipe(
                    title = title,
                    description = description,
                    ingredients = ingredients,
                    instructions = instructions,
                    prepTime = prepTime,
                    cookTime = cookTime,
                    servings = servings,
                    imageUrl = imageUrl,
                    isUserCreated = true
                )
            }

            // Save recipe to database
            if (editingRecipe != null) {
                viewModel.update(recipe)
                Toast.makeText(this, "Recipe updated", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insert(recipe)
                Toast.makeText(this, "Recipe saved", Toast.LENGTH_SHORT).show()
            }

            // Close activity
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "saveRecipe: Error saving recipe", e)
            Toast.makeText(this, "Error saving recipe: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }
}