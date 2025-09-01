package com.leo.paleorecipes.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // RecipeRepository is now provided in AppModule to avoid circular dependencies
}
