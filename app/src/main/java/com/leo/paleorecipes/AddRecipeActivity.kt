package com.leo.paleorecipes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.leo.paleorecipes.databinding.ActivityAddRecipeBinding
import com.leo.paleorecipes.db.RecipeDatabase
import com.leo.paleorecipes.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecipeBinding
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                selectedImageUri = Uri.fromFile(File(path))
                binding.recipeImage.setImageURI(selectedImageUri)
            }
        }
    }
    
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.recipeImage.setImageURI(selectedImageUri)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupCategorySpinner()
        setupDifficultySpinner()
        setupButtons()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_recipe)
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupCategorySpinner() {
        val categories = arrayOf(
            getString(R.string.category_breakfast),
            getString(R.string.category_lunch),
            getString(R.string.category_dinner),
            getString(R.string.category_snack),
            getString(R.string.category_dessert),
            getString(R.string.category_appetizer),
            getString(R.string.category_side_dish),
            getString(R.string.category_soup),
            getString(R.string.category_salad),
            getString(R.string.category_beverage)
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter
    }
    
    private fun setupDifficultySpinner() {
        val difficulties = arrayOf(
            getString(R.string.difficulty_easy),
            getString(R.string.difficulty_medium),
            getString(R.string.difficulty_hard)
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficulties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.difficultySpinner.adapter = adapter
    }
    
    private fun setupButtons() {
        binding.buttonAddPhoto.setOnClickListener {
            showImagePickerOptions()
        }
        
        binding.buttonSave.setOnClickListener {
            saveRecipe()
        }
        
        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun showImagePickerOptions() {
        val options = arrayOf(
            getString(R.string.button_take_photo),
            getString(R.string.button_choose_from_gallery)
        )
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.button_add_photo))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhoto()
                    1 -> chooseFromGallery()
                }
            }
            .show()
    }
    
    private fun takePhoto() {
        val photoFile = createImageFile()
        photoFile?.let {
            val photoURI = FileProvider.getUriForFile(
                this,
                "com.leo.paleorecipes.fileprovider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }
    
    private fun chooseFromGallery() {
        pickImageLauncher.launch("image/*")
    }
    
    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = getExternalFilesDir(null)
            val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )
            currentPhotoPath = image.absolutePath
            image
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun saveRecipe() {
        val name = binding.editTextName.text.toString().trim()
        val ingredients = binding.editTextIngredients.text.toString().trim()
        val instructions = binding.editTextInstructions.text.toString().trim()
        val prepTimeStr = binding.editTextPrepTime.text.toString().trim()
        val cookTimeStr = binding.editTextCookTime.text.toString().trim()
        val servingsStr = binding.editTextServings.text.toString().trim()
        val category = binding.categorySpinner.selectedItem.toString()
        val difficulty = binding.difficultySpinner.selectedItem.toString()
        val notes = binding.editTextNotes.text.toString().trim()
        
        // Validation
        if (name.isEmpty()) {
            binding.editTextName.error = getString(R.string.error_empty_name)
            return
        }
        
        if (ingredients.isEmpty()) {
            binding.editTextIngredients.error = getString(R.string.error_empty_ingredients)
            return
        }
        
        if (instructions.isEmpty()) {
            binding.editTextInstructions.error = getString(R.string.error_empty_instructions)
            return
        }
        
        val prepTime = if (prepTimeStr.isNotEmpty()) prepTimeStr.toIntOrNull() ?: 0 else 0
        val cookTime = if (cookTimeStr.isNotEmpty()) cookTimeStr.toIntOrNull() ?: 0 else 0
        val servings = if (servingsStr.isNotEmpty()) servingsStr.toIntOrNull() ?: 0 else 0
        
        val recipe = Recipe(
            id = 0, // Room will auto-generate the ID
            name = name,
            ingredients = ingredients,
            instructions = instructions,
            prepTime = prepTime,
            cookTime = cookTime,
            servings = servings,
            category = category,
            difficulty = difficulty,
            notes = notes,
            imageUri = selectedImageUri?.toString() ?: "",
            isFavorite = false,
            dateAdded = System.currentTimeMillis()
        )
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = RecipeDatabase.getDatabase(this@AddRecipeActivity)
                db.recipeDao().insert(recipe)
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddRecipeActivity,
                        getString(R.string.success_recipe_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddRecipeActivity,
                        getString(R.string.error_saving_recipe),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}