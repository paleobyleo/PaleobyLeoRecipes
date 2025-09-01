package com.leo.paleorecipes.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.leo.paleorecipes.data.local.dao.IngredientDao
import com.leo.paleorecipes.data.local.entity.IngredientEntity

@Database(
    entities = [
        Recipe::class,
        IngredientEntity::class,
    ],
    version = 3, // Incremented version
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao

    companion object {
        private const val TAG = "AppDatabase"

        // Migration from version 1 to 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add any necessary schema changes here
                // For now, we're just updating the version number
            }
        }

        // Migration from version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add any necessary schema changes here
                // For now, we're just updating the version number
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    Log.d(TAG, "Creating database instance")
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "paleo_recipes_database",
                    )
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                        .build()
                    INSTANCE = instance
                    Log.d(TAG, "Database instance created successfully")
                    instance
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating database", e)
                    throw e
                }
            }
        }
    }
}
