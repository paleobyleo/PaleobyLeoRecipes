package com.leo.paleorecipes.di

import android.content.Context
import com.leo.paleorecipes.data.AppDatabase
import com.leo.paleorecipes.data.RecipeDao
import com.leo.paleorecipes.data.local.dao.IngredientDao
import com.leo.paleorecipes.data.repository.IngredientRepository
import com.leo.paleorecipes.data.repository.IngredientRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun provideIngredientDao(database: AppDatabase): IngredientDao {
        return database.ingredientDao()
    }

    @Provides
    @Singleton
    fun provideIngredientRepository(
        ingredientDao: IngredientDao,
    ): IngredientRepository {
        return IngredientRepositoryImpl(ingredientDao)
    }
}
