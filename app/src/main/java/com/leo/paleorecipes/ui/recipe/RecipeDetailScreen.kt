package com.leo.paleorecipes.ui.recipe

import android.content.Context
import android.content.Intent
import android.print.PrintManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leo.paleorecipes.AddEditRecipeActivity
import com.leo.paleorecipes.RecipePrintAdapter
import com.leo.paleorecipes.data.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: Recipe,
    onNavigateBack: () -> Unit,
    onEditRecipe: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "View Recipe",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditRecipe) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
                    }
                    IconButton(onClick = { printRecipe(context, recipe) }) {
                        Icon(Icons.Default.Print, contentDescription = "Print Recipe")
                    }
                },
                // Apply saddle brown colors to the top bar to match Add Recipe form
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B4513), // SaddleBrown
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp)
                // Apply solid black background to match Add Recipe form
                .background(Color.Black),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Basic Information Section (matching Add Recipe form)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White, // White text
                    )

                    OutlinedTextField(
                        value = recipe.title,
                        onValueChange = {},
                        label = { Text("Recipe Title") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false, // Make read-only
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                            unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                            focusedLabelColor = Color.White, // White label
                            unfocusedLabelColor = Color.White, // White label
                            disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                            disabledLabelColor = Color.White, // White label
                            disabledTextColor = Color.White, // White text
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                    )

                    OutlinedTextField(
                        value = recipe.description,
                        onValueChange = {},
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false, // Make read-only
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                            unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                            focusedLabelColor = Color.White, // White label
                            unfocusedLabelColor = Color.White, // White label
                            disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                            disabledLabelColor = Color.White, // White label
                            disabledTextColor = Color.White, // White text
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                        maxLines = 5,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = recipe.category,
                            onValueChange = {},
                            label = { Text("Category") },
                            modifier = Modifier.weight(1f),
                            enabled = false, // Make read-only
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = Color.White, // White label
                                unfocusedLabelColor = Color.White, // White label
                                disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                disabledLabelColor = Color.White, // White label
                                disabledTextColor = Color.White, // White text
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                        )

                        OutlinedTextField(
                            value = recipe.servings.toString(),
                            onValueChange = {},
                            label = { Text("Servings") },
                            modifier = Modifier.weight(0.7f),
                            enabled = false, // Make read-only
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = Color.White, // White label
                                unfocusedLabelColor = Color.White, // White label
                                disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                disabledLabelColor = Color.White, // White label
                                disabledTextColor = Color.White, // White text
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = recipe.prepTime.toString(),
                            onValueChange = {},
                            label = { Text("Prep Time (min)") },
                            modifier = Modifier.weight(1f),
                            enabled = false, // Make read-only
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = Color.White, // White label
                                unfocusedLabelColor = Color.White, // White label
                                disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                disabledLabelColor = Color.White, // White label
                                disabledTextColor = Color.White, // White text
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                        )

                        OutlinedTextField(
                            value = recipe.cookTime.toString(),
                            onValueChange = {},
                            label = { Text("Cook Time (min)") },
                            modifier = Modifier.weight(1f),
                            enabled = false, // Make read-only
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = Color.White, // White label
                                unfocusedLabelColor = Color.White, // White label
                                disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                disabledLabelColor = Color.White, // White label
                                disabledTextColor = Color.White, // White text
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                        )
                    }
                }
            }

            // Ingredients Section (matching Add Recipe form)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White, // White text
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    recipe.ingredients.forEachIndexed { index, ingredient ->
                        if (ingredient.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedTextField(
                                    value = ingredient,
                                    onValueChange = {},
                                    label = { Text("Ingredient ${index + 1}") },
                                    modifier = Modifier.weight(1f),
                                    enabled = false, // Make read-only
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                        unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                        focusedLabelColor = Color.White, // White label
                                        unfocusedLabelColor = Color.White, // White label
                                        disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                        disabledLabelColor = Color.White, // White label
                                        disabledTextColor = Color.White, // White text
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                                )
                            }
                        }
                    }
                }
            }

            // Instructions Section (matching Add Recipe form)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White, // White text
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    recipe.instructions.forEachIndexed { index, instruction ->
                        if (instruction.isNotBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedTextField(
                                    value = instruction,
                                    onValueChange = {},
                                    label = { Text("Step ${index + 1}") },
                                    modifier = Modifier.weight(1f),
                                    enabled = false, // Make read-only
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                        unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                        focusedLabelColor = Color.White, // White label
                                        unfocusedLabelColor = Color.White, // White label
                                        disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                        disabledLabelColor = Color.White, // White label
                                        disabledTextColor = Color.White, // White text
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                                    maxLines = 3,
                                )
                            }
                        }
                    }
                }
            }

            // Notes Section (matching Add Recipe form)
            if (recipe.notes.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "Additional Notes",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White, // White text
                        )

                        OutlinedTextField(
                            value = recipe.notes,
                            onValueChange = {},
                            label = { Text("Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false, // Make read-only
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF8B4513), // SaddleBrown
                                unfocusedBorderColor = Color(0xFF8B4513).copy(alpha = 0.7f), // SaddleBrown with transparency
                                focusedLabelColor = Color.White, // White label
                                unfocusedLabelColor = Color.White, // White label
                                disabledBorderColor = Color(0xFF8B4513), // SaddleBrown
                                disabledLabelColor = Color.White, // White label
                                disabledTextColor = Color.White, // White text
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White), // White text
                            maxLines = 4,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun printRecipe(context: Context, recipe: Recipe) {
    try {
        Log.d("RecipeDetailScreen", "Printing recipe: ${recipe.title}")

        // Create a print manager
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

        // Create a print adapter
        val printAdapter = RecipePrintAdapter(context, recipe)

        // Create a print job name
        val jobName = "${recipe.title} Recipe"

        // Create a print job
        printManager.print(jobName, printAdapter, null)

        Toast.makeText(context, "Preparing to print ${recipe.title}", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e("RecipeDetailScreen", "Error printing recipe: ${e.message}", e)
        Toast.makeText(context, "Error printing recipe: ${e.message}", Toast.LENGTH_LONG).show()
    }
}