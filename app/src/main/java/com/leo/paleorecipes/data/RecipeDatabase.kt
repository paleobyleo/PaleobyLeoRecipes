package com.leo.paleorecipes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(RecipeDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class RecipeDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.recipeDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(recipeDao: RecipeDao) {
            // Add some sample paleo recipes
            val sampleRecipes = listOf(
                Recipe(
                    title = "Paleo Chicken Stir-Fry",
                    description = "A quick and healthy paleo chicken stir-fry",
                    ingredients = listOf(
                        "2 chicken breasts",
                        "1 bell pepper",
                        "1 onion",
                        "2 tbsp olive oil",
                        "salt and pepper"
                    ),
                    instructions = listOf(
                        "Cut chicken into strips",
                        "Heat oil in pan",
                        "Cook chicken until done",
                        "Add vegetables and stir-fry"
                    ),
                    prepTime = 15,
                    cookTime = 20,
                    servings = 2,
                    imageUrl = "https://example.com/chicken-stir-fry.jpg",
                    isUserCreated = false
                ),
                Recipe(
                    title = "Beef and Vegetable Soup",
                    description = "Hearty paleo beef and vegetable soup",
                    ingredients = listOf(
                        "1 lb grass-fed beef",
                        "2 carrots",
                        "1 onion",
                        "2 celery stalks",
                        "4 cups bone broth"
                    ),
                    instructions = listOf(
                        "Brown beef in pot",
                        "Add vegetables and broth",
                        "Simmer for 1 hour",
                        "Season to taste"
                    ),
                    prepTime = 20,
                    cookTime = 60,
                    servings = 4,
                    imageUrl = "https://example.com/beef-soup.jpg",
                    isUserCreated = false
                )
            )

            sampleRecipes.forEach { recipeDao.insert(it) }
        }
    }
}