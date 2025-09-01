package com.leo.paleorecipes.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPaleoScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "About Paleo Diet",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // Apply saddle brown colors to the top bar to match other screens
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
                // Apply solid black background to match other screens
                .background(Color.Black),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "The Paleo Diet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "The Paleolithic Diet, often referred to as the Paleo Diet, is a nutritional approach based on foods similar to those consumed during the Paleolithic era, spanning from approximately 2.5 million to 10,000 years ago. This dietary framework emphasizes whole foods that could be obtained through hunting and gathering, while excluding processed foods, grains, legumes, and dairy products that became common with the advent of agriculture.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        lineHeight = 24.sp,
                    )
                }
            }

            // Core Principles Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Core Principles",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val principles = listOf(
                        "Emphasize whole, unprocessed foods",
                        "Eliminate modern processed foods",
                        "High-quality protein consumption",
                        "Moderate to lower carbohydrate intake",
                        "High fiber from fruits and vegetables",
                        "Healthy fat consumption from nuts, seeds, and oils",
                        "Nutrient density optimization",
                        "Balanced omega-3 to omega-6 fatty acid ratio"
                    )
                    
                    principles.forEach { principle ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = principle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            // Recommended Foods Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Recommended Foods",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF8B4513), // SaddleBrown
                                contentColor = Color.White, // White text
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), // White lining
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = "QUALITY PROTEINS",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        
                        OutlinedButton(
                            onClick = { },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF8B4513), // SaddleBrown
                                contentColor = Color.White, // White text
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), // White lining
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                text = "FRESH PRODUCE",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }

            // Foods to Include Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Foods to Include",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val foodsToInclude = listOf(
                        "Meats: Grass-fed beef, lamb, pork, etc.",
                        "Poultry: Chicken, turkey, duck, etc.",
                        "Fish and seafood: Wild-caught fish, shellfish",
                        "Eggs: Preferably free-range",
                        "Vegetables: All types, especially leafy greens",
                        "Fruits: All types, preferably low-glycemic",
                        "Nuts and seeds: Almonds, walnuts, sunflower seeds, etc.",
                        "Healthy oils: Olive oil, coconut oil, avocado oil"
                    )
                    
                    foodsToInclude.forEach { food ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = food,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            // Foods to Avoid Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Foods to Avoid",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val foodsToAvoid = listOf(
                        "Grains: Wheat, oats, barley, etc.",
                        "Legumes: Beans, lentils, peanuts, etc.",
                        "Dairy: Milk, cheese, yogurt, etc.",
                        "Refined sugar: Candy, soft drinks, most packaged sweets",
                        "Potatoes: Especially white potatoes",
                        "Highly processed foods: Anything with artificial ingredients",
                        "Salt: Especially refined table salt",
                        "Refined vegetable oils: Canola, soybean, etc."
                    )
                    
                    foodsToAvoid.forEach { food ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = food,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            // Health Benefits Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Health Benefits",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val benefits = listOf(
                        "Weight loss and improved body composition",
                        "Better blood sugar control",
                        "Improved energy levels",
                        "Reduced inflammation",
                        "Better digestion",
                        "Improved nutrient density",
                        "Enhanced mental clarity",
                        "Better sleep quality"
                    )
                    
                    benefits.forEach { benefit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp),
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = benefit,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            // Note Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF8B4513).copy(alpha = 0.1f), // Light saddle brown
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "Note: While many people report health improvements on the Paleo diet, individual results may vary. Always consult with a healthcare professional before making significant dietary changes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}