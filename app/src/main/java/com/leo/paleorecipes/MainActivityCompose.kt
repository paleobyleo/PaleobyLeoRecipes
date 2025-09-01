package com.leo.paleorecipes

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.leo.paleorecipes.data.Recipe
import com.leo.paleorecipes.ui.theme.PaleoRecipesTheme
import com.leo.paleorecipes.viewmodel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure status bar is visible and stays visible
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Show status bar and prevent it from being hidden
        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Set up a listener to ensure status bar stays visible
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // If status bar is hidden, show it again
            if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
                windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
            }
        }

        setContent {
            // Set solid black background for the entire app
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            ) {
                PaleoRecipesTheme {
                    // Add more padding to avoid overlapping with status bar and create space
                    Box(modifier = Modifier.fillMaxSize().padding(top = 48.dp)) {
                        MainScreen()
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Ensure status bar is visible when window gains focus
            val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
            windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onEditClick: () -> Unit,
    onPrintClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onFavoriteClick: (Recipe) -> Unit,
    onViewClick: () -> Unit, // Add view click parameter
) {
    // New frame design with clear borders and better visibility
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(
                border = BorderStroke(2.dp, Color(0xFF8B4513)), // SaddleBrown border
                shape = RoundedCornerShape(16.dp),
            )
            .background(Color(0xFF1E1E1E)), // Dark background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Reduced padding
        ) {
            // Header with title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = recipe.title.ifEmpty { "Untitled Recipe" },
                    color = Color.White,
                    fontSize = 18.sp, // Slightly smaller font
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { onFavoriteClick(recipe) },
                    modifier = Modifier
                        .size(36.dp), // Slightly smaller icon button
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (recipe.isFavorite) Color(0xFF006400) else Color.White,
                        modifier = Modifier.size(20.dp), // Smaller icon
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp)) // Reduced spacing

            // Category and Cook Time as buttons - reduced width
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp), // Reduced spacing
            ) {
                // Category button - reduced width
                OutlinedButton(
                    onClick = { /* Handle category click if needed */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF8B4513), // SaddleBrown background color
                        contentColor = Color.White, // White text color
                    ),
                    border = BorderStroke(1.dp, Color.White), // White lining
                    modifier = Modifier
                        .weight(0.4f) // Reduced width
                        .height(32.dp), // Smaller height
                ) {
                    Text(
                        text = if (recipe.category.isNotBlank()) recipe.category else "No Category",
                        color = Color.White,
                        fontSize = 11.sp, // Smaller font
                    )
                }

                // Cook Time button - reduced width
                OutlinedButton(
                    onClick = { /* Handle cook time click if needed */ },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF8B4513), // SaddleBrown background color
                        contentColor = Color.White, // White text color
                    ),
                    border = BorderStroke(1.dp, Color.White), // White lining
                    modifier = Modifier
                        .weight(0.4f) // Reduced width
                        .height(32.dp), // Smaller height
                ) {
                    Text(
                        text = if (recipe.cookTime > 0) "${recipe.cookTime} min" else "No Time",
                        color = Color.White,
                        fontSize = 11.sp, // Smaller font
                    )
                }

                // Spacer to take up remaining space
                Spacer(modifier = Modifier.weight(0.2f))
            }

            Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

            // Description - smaller text
            Text(
                text = "Description:",
                color = Color(0xFF8B4513),
                fontSize = 14.sp, // Smaller font
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacing

            Text(
                text = if (recipe.description.isNotBlank()) recipe.description else "No description available",
                color = Color.White,
                fontSize = 12.sp, // Smaller font
            )

            Spacer(modifier = Modifier.height(8.dp)) // Reduced spacing

            // First ingredient only - smaller text
            Text(
                text = "Ingredients:",
                color = Color(0xFF8B4513),
                fontSize = 14.sp, // Smaller font
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacing

            Text(
                text = if (recipe.ingredients.isNotEmpty()) "• ${recipe.ingredients.first()}" else "No ingredients listed",
                color = Color.White,
                fontSize = 12.sp, // Smaller font
                modifier = Modifier.padding(start = 6.dp), // Reduced padding
            )

            Spacer(modifier = Modifier.height(12.dp)) // Reduced spacing

            // Action buttons - smaller rectangular icons only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                // View button - smaller rectangular with only icon
                Button(
                    onClick = onViewClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2), // Blue for view
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp) // Smaller rectangular shape
                        .padding(horizontal = 1.dp), // Reduced padding
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "View",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp), // Smaller icon
                    )
                }

                // Edit button - smaller rectangular with only icon
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF006400), // Dark green
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp) // Smaller rectangular shape
                        .padding(horizontal = 1.dp), // Reduced padding
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp), // Smaller icon
                    )
                }

                // Print button - smaller rectangular with only icon
                Button(
                    onClick = onPrintClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4513), // SaddleBrown
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp) // Smaller rectangular shape
                        .padding(horizontal = 1.dp), // Reduced padding
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp), // Smaller icon
                    )
                }

                // Delete button - smaller rectangular with only icon
                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB22222), // Dark red
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp) // Smaller rectangular shape
                        .padding(horizontal = 1.dp), // Reduced padding
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp), // Smaller icon
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RecipeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(true) } // Changed default to true
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect recipes from ViewModel
    val recipes by viewModel.allUserRecipes.observeAsState(emptyList())
    val searchResults by viewModel.searchResults.observeAsState(emptyList())

    // Filter recipes based on favorites
    val filteredRecipes = remember(recipes, searchResults, searchQuery, showFavoritesOnly) {
        val baseRecipes = if (searchQuery.isNotBlank()) searchResults else recipes
        baseRecipes.filter { recipe ->
            !showFavoritesOnly || recipe.isFavorite
        }
    }

    // Load recipes on first launch
    LaunchedEffect(Unit) {
        viewModel.loadAllUserRecipes()
    }

    // Handle search query changes with debounce to prevent cancellation
    LaunchedEffect(searchQuery) {
        // Always clear previous results first
        viewModel.clearSearchResults()

        if (searchQuery.isNotBlank()) {
            delay(300) // Debounce to reduce API calls
            try {
                viewModel.searchUserRecipes(searchQuery)
            } catch (e: Exception) {
                // Handle cancellation gracefully
                if (e !is kotlinx.coroutines.CancellationException) {
                    viewModel.clearSearchResults()
                }
            }
        }
    }

    // Handle delete confirmation dialog
    if (showDeleteDialog && selectedRecipe != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete '${selectedRecipe?.title}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedRecipe?.let { recipe ->
                            viewModel.deleteRecipe(recipe)
                            showDeleteDialog = false
                            selectedRecipe = null
                        }
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedRecipe = null
                    },
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .align(Alignment.Center)
                        .padding(vertical = 8.dp)
                        .border( // Add saddle brown border
                            border = BorderStroke(2.dp, Color(0xFF8B4513)), // SaddleBrown color
                            shape = RoundedCornerShape(16.dp),
                        ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E),
                    ),
                    shape = RoundedCornerShape(16.dp),
                    // Remove the border parameter since we're using the modifier above
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        // Header with app branding - Added back the restaurant icon
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Paleo Recipes",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            // Your custom logo - placed beneath the text
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.paleo_logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(75.dp) // Increased size to ~75dp
                                    .padding(top = 8.dp)
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linktr.ee/paleobyleo"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Handle case where no browser is available
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Unable to open browser")
                                            }
                                        }
                                    },
                                contentScale = ContentScale.Fit,
                            )

                            // Display the URL text under the logo and make it clickable
                            Text(
                                text = "https://linktr.ee/paleobyleo",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF8B4513), // SaddleBrown color for visibility
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://linktr.ee/paleobyleo"))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            // Handle case where no browser is available
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Unable to open browser")
                                            }
                                        }
                                    },
                            )
                        }

                        // Search bar
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.7f) // Reduced width from 0.9f to 0.7f to make it narrower
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2C2C2C),
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF8B4513)), // Saddle brown lining instead of white
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp), // Height of the search field
                                placeholder = {
                                    Text(
                                        text = "Search...", // Brighter text color
                                        color = Color.White, // Make text brighter
                                        style = MaterialTheme.typography.bodyMedium, // Text size
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.White, // Make icon brighter
                                        modifier = Modifier.size(16.dp), // Icon size
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchQuery = "" }, // Clear the search query
                                            modifier = Modifier.size(24.dp), // Icon button size
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear search",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp), // Icon size
                                            )
                                        }
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFF2C2C2C),
                                    unfocusedContainerColor = Color(0xFF2C2C2C),
                                    focusedTextColor = Color.White, // Make text brighter
                                    unfocusedTextColor = Color.White, // Make text brighter
                                    cursorColor = Color.White, // Make cursor brighter
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedPlaceholderColor = Color.White, // Make placeholder brighter
                                    unfocusedPlaceholderColor = Color.White, // Make placeholder brighter
                                ),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White), // Text size
                            )
                        }

                        // Filter section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            FilterChip(
                                selected = showFavoritesOnly,
                                onClick = { showFavoritesOnly = !showFavoritesOnly },
                                label = {
                                    Text(
                                        "Favorites",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = if (showFavoritesOnly) Color.White else Color.White, // Make text brighter
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (showFavoritesOnly) {
                                            Icons.Filled.Favorite
                                        } else {
                                            Icons.Outlined.FavoriteBorder
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = if (showFavoritesOnly) Color(0xFF006400) else Color.White, // Solid green when selected, white when not
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8B4513),
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color(0xFF006400), // Solid green when selected
                                    containerColor = Color(0xFF2C2C2C),
                                    labelColor = Color.White, // Make text brighter
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = Color.White, // Make favorites button lining white
                                    selectedBorderColor = Color.White, // Make favorites button lining white
                                    borderWidth = 1.5.dp,
                                ),
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            // Recipe count
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Text(
                                    text = "+",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF8B4513),
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2C2C2C),
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                ) {
                                    Text(
                                        text = "${filteredRecipes.size} recipes",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF8B4513),
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Floating action button
                            FloatingActionButton(
                                onClick = {
                                    val intent = Intent(context, AddEditRecipeActivity::class.java)
                                    context.startActivity(intent)
                                },
                                containerColor = Color(0xFF8B4513),
                                contentColor = Color.White,
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 8.dp,
                                    pressedElevation = 12.dp,
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(
                                        border = BorderStroke(1.dp, Color.White),
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add Recipe",
                                    modifier = Modifier.size(20.dp),
                                )
                            }

                            // OCR Scan button - REMOVED as it's now integrated in the Add Recipe form
                        }

                        // Add a button to navigate to RecipeListActivity for export/import on a new row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(context, RecipeListActivity::class.java).apply {
                                        putExtra("isUserRecipes", true)
                                    }
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B4513),
                                    contentColor = Color.White,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f) // Reduced from 0.8f to 0.6f
                                    .height(40.dp) // Reduced from 48.dp to 40.dp
                                    .border(
                                        border = BorderStroke(1.dp, Color.White),
                                        shape = RoundedCornerShape(12.dp), // Reduced corner radius
                                    ),
                                shape = RoundedCornerShape(12.dp), // Reduced corner radius
                            ) {
                                Text(
                                    text = "Import/Export Recipes",
                                    style = MaterialTheme.typography.labelMedium, // Changed from labelLarge to labelMedium
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Add About Paleo button on a new row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(context, AboutPaleoActivity::class.java)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8B4513),
                                    contentColor = Color.White,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(40.dp)
                                    .border(
                                        border = BorderStroke(1.dp, Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                    ),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text(
                                    text = "About Paleo",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        // Main content with recipe list
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(filteredRecipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onEditClick = {
                            // Fix: Pass the full recipe object instead of just the ID
                            val intent = Intent(context, AddEditRecipeActivity::class.java).apply {
                                putExtra("recipe", recipe)
                            }
                            context.startActivity(intent)
                        },
                        onPrintClick = {
                            // Fix: Implement actual print functionality instead of just showing a snackbar
                            try {
                                val printManager = context.getSystemService(Context.PRINT_SERVICE) as android.print.PrintManager
                                val printAdapter = com.leo.paleorecipes.RecipePrintAdapter(context, recipe)
                                val jobName = "${recipe.title} Recipe"
                                printManager.print(jobName, printAdapter, null)

                                scope.launch {
                                    snackbarHostState.showSnackbar("Preparing to print ${recipe.title}")
                                }
                            } catch (e: Exception) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error printing recipe: ${e.message}")
                                }
                            }
                        },
                        onDeleteClick = {
                            selectedRecipe = recipe
                            showDeleteDialog = true
                        },
                        onFavoriteClick = { updatedRecipe ->
                            viewModel.toggleFavorite(updatedRecipe)
                        },
                        onViewClick = {
                            // Add view functionality to open RecipeDetailActivity
                            val intent = Intent(context, com.leo.paleorecipes.RecipeDetailActivity::class.java).apply {
                                putExtra("recipe", recipe)
                            }
                            context.startActivity(intent)
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Temporarily disable AdMob Banner Ad
            // AdMobBanner(
            //     modifier = Modifier
            //         .fillMaxWidth()
            //         .padding(horizontal = 16.dp, vertical = 8.dp)
            // )

            // Copyright message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "© 2025 Paleo by Leo. MIT License.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
