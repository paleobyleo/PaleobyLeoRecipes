package com.leo.paleorecipes.ui.recipe

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leo.paleorecipes.OcrScanActivity
import com.leo.paleorecipes.data.Recipe

@Composable
fun VisibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    maxLines: Int = 1,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = androidx.compose.ui.graphics.Color.White, // White text
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 24.dp),
                textStyle = TextStyle(
                    color = androidx.compose.ui.graphics.Color.White, // White text
                    fontSize = 16.sp,
                ),
                cursorBrush = SolidColor(androidx.compose.ui.graphics.Color(0xFF8B4513)), // SaddleBrown
                keyboardOptions = keyboardOptions,
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                maxLines = maxLines,
                singleLine = maxLines == 1,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                "Enter $label",
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), // White with transparency
                                fontSize = 16.sp,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    recipe: Recipe? = null,
    onSave: (Recipe) -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var title by remember { mutableStateOf(recipe?.title ?: "") }
    var description by remember { mutableStateOf(recipe?.description ?: "") }
    var category by remember { mutableStateOf(recipe?.category ?: "") }
    var servings by remember { mutableStateOf(recipe?.servings?.toString() ?: "1") }
    var prepTime by remember { mutableStateOf(recipe?.prepTime?.toString() ?: "0") }
    var cookTime by remember { mutableStateOf(recipe?.cookTime?.toString() ?: "0") }
    var ingredients by remember { mutableStateOf(recipe?.ingredients?.toMutableList() ?: mutableListOf("")) }
    var instructions by remember { mutableStateOf(recipe?.instructions?.toMutableList() ?: mutableListOf("")) }
    var notes by remember { mutableStateOf(recipe?.notes ?: "") }

    val isEditing = recipe != null
    // Only require title to be non-blank, make all other fields optional
    val isValid = title.isNotBlank()

    fun handleSave() {
        if (isValid) {
            val newRecipe = Recipe(
                id = recipe?.id ?: 0L,
                title = title.trim(),
                description = description.trim(),
                ingredients = ingredients.filter { it.isNotBlank() },
                instructions = instructions.filter { it.isNotBlank() },
                prepTime = prepTime.toIntOrNull() ?: 0,
                cookTime = cookTime.toIntOrNull() ?: 0,
                servings = servings.toIntOrNull() ?: 1,
                imageUrl = recipe?.imageUrl ?: "",
                category = category.trim(),
                difficulty = recipe?.difficulty ?: "",
                notes = notes.trim(),
                isUserCreated = true,
                isFavorite = recipe?.isFavorite ?: false,
                dateAdded = recipe?.dateAdded ?: System.currentTimeMillis(),
            )
            onSave(newRecipe)
        } else {
            // Show a message to the user about what's missing
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Missing Required Information")
                .setMessage("Please provide a title for the recipe.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun launchOcrScanner() {
        try {
            val intent = Intent(context, OcrScanActivity::class.java)
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AddEditRecipeScreen", "Error launching OCR scanner: ${e.message}", e)
            // Show a more user-friendly message
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("OCR Not Available")
                .setMessage("The OCR scanning feature is not available on your device. You can manually enter the recipe details instead.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Recipe" else "Add Recipe",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                },
                actions = {
                    IconButton(onClick = { launchOcrScanner() }) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = "Scan Recipe",
                        )
                    }
                    IconButton(onClick = { handleSave() }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                },
                // Apply saddle brown colors to the top bar
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White,
                    actionIconContentColor = androidx.compose.ui.graphics.Color.White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
                // Apply solid black background to the entire form
                .background(androidx.compose.ui.graphics.Color.Black), // Solid black background
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Show a banner for OCR scanned recipes
            if (recipe?.category == "Scanned" && !isEditing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.2f), // SaddleBrown with transparency
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = androidx.compose.ui.graphics.Color.White, // White icon
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OCR Scanned Recipe",
                                style = MaterialTheme.typography.titleMedium,
                                color = androidx.compose.ui.graphics.Color.White, // White text
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This recipe was created from an OCR scan. Please review and edit the title, add ingredients and instructions, and make any necessary corrections.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color.White, // White text
                        )

                        // Add additional help text for OCR issues
                        if (recipe.description.contains("LIMITED TEXT DETECTED") ||
                            recipe.description.length < 100
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Note: The OCR scan detected limited text. " +
                                    "You can still save this recipe and manually edit the content. " +
                                    "For better results, try scanning again with improved lighting and focus.",
                                style = MaterialTheme.typography.bodySmall,
                                color = androidx.compose.ui.graphics.Color.White, // White text
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                }
            }

            // Basic Information Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color.White, // White text
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Recipe Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                            focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                            unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                            cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                            focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                        ),
                        textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                            focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                            unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                            cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                            focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                        ),
                        textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        ),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                                unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            ),
                            textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            ),
                        )

                        OutlinedTextField(
                            value = servings,
                            onValueChange = { servings = it },
                            label = { Text("Servings") },
                            modifier = Modifier.weight(0.7f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                                unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            ),
                            textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = prepTime,
                            onValueChange = { prepTime = it },
                            label = { Text("Prep Time (min)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                                unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            ),
                            textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                        )

                        OutlinedTextField(
                            value = cookTime,
                            onValueChange = { cookTime = it },
                            label = { Text("Cook Time (min)") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                                cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                                focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                                unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            ),
                            textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            ),
                        )
                    }
                }
            }

            // Ingredients Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Ingredients", // Removed asterisk to indicate it's optional
                            style = MaterialTheme.typography.titleMedium,
                            color = androidx.compose.ui.graphics.Color.White, // White text
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(
                            onClick = { ingredients = (ingredients + "").toMutableList() },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Ingredient", tint = androidx.compose.ui.graphics.Color.White) // White icon
                        }
                    }

                    ingredients.forEachIndexed { index, ingredient ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            VisibleTextField(
                                value = ingredient,
                                onValueChange = { newValue ->
                                    val updatedIngredients = ingredients.toMutableList()
                                    updatedIngredients[index] = newValue
                                    ingredients = updatedIngredients
                                },
                                label = "Ingredient ${index + 1}",
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            )

                            if (ingredients.size > 1) {
                                IconButton(
                                    onClick = {
                                        val updatedIngredients = ingredients.toMutableList()
                                        updatedIngredients.removeAt(index)
                                        ingredients = updatedIngredients
                                    },
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = androidx.compose.ui.graphics.Color.White) // White icon
                                }
                            }
                        }
                    }
                }
            }

            // Instructions Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Instructions", // Removed asterisk to indicate it's optional
                            style = MaterialTheme.typography.titleMedium,
                            color = androidx.compose.ui.graphics.Color.White, // White text
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(
                            onClick = { instructions = (instructions + "").toMutableList() },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Step", tint = androidx.compose.ui.graphics.Color.White) // White icon
                        }
                    }

                    instructions.forEachIndexed { index, instruction ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            VisibleTextField(
                                value = instruction,
                                onValueChange = { newValue ->
                                    val updatedInstructions = instructions.toMutableList()
                                    updatedInstructions[index] = newValue
                                    instructions = updatedInstructions
                                },
                                label = "Step ${index + 1}",
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                maxLines = 3,
                            )

                            if (instructions.size > 1) {
                                IconButton(
                                    onClick = {
                                        val updatedInstructions = instructions.toMutableList()
                                        updatedInstructions.removeAt(index)
                                        instructions = updatedInstructions
                                    },
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = androidx.compose.ui.graphics.Color.White) // White icon
                                }
                            }
                        }
                    }
                }
            }

            // Notes Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Additional Notes",
                        style = MaterialTheme.typography.titleMedium,
                        color = androidx.compose.ui.graphics.Color.White, // White text
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                            focusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                            unfocusedLabelColor = androidx.compose.ui.graphics.Color.White, // White label
                            cursorColor = androidx.compose.ui.graphics.Color(0xFF8B4513), // SaddleBrown
                            focusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                            unfocusedTextColor = androidx.compose.ui.graphics.Color.White, // White text
                        ),
                        textStyle = TextStyle(color = androidx.compose.ui.graphics.Color.White), // White text
                        maxLines = 4,
                        placeholder = {
                            Text(
                                "Add any tips, substitutions, or special notes...",
                                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), // White with transparency
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Wrapper function that matches the signature expected by AddEditRecipeActivity
 */
@Composable
fun AddEditRecipeScreen(
    recipe: Recipe? = null,
    onSaveRecipe: (Recipe) -> Unit,
    onNavigateBack: () -> Unit,
) {
    RecipeForm(
        recipe = recipe,
        onSave = onSaveRecipe,
        onCancel = onNavigateBack,
    )
}
