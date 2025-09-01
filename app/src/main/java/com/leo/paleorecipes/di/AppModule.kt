package com.leo.paleorecipes.di

import android.content.Context
import androidx.work.WorkManager
import com.leo.paleorecipes.data.RecipeDao
import com.leo.paleorecipes.data.api.ApiClient
import com.leo.paleorecipes.data.api.SpoonacularApiService
import com.leo.paleorecipes.data.repository.RecipeRepository
import com.leo.paleorecipes.data.repository.RecipeRepositoryImpl
import com.leo.paleorecipes.utils.ConnectivityManagerNetworkMonitor
import com.leo.paleorecipes.utils.FileUtils
import com.leo.paleorecipes.utils.NetworkMonitor
import com.leo.paleorecipes.utils.RecipeBackupManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeDao: RecipeDao,
        apiClient: ApiClient,
        networkMonitor: NetworkMonitor,
    ): RecipeRepository {
        return RecipeRepositoryImpl(recipeDao, apiClient, networkMonitor)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSpoonacularApiService(retrofit: Retrofit): SpoonacularApiService {
        return retrofit.create(SpoonacularApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiClient(apiService: SpoonacularApiService): ApiClient {
        return ApiClient(apiService)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor {
        return ConnectivityManagerNetworkMonitor(context)
    }

    @Provides
    @Singleton
    @Named("io_dispatcher")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    @Named("main_dispatcher")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFileUtils(@ApplicationContext context: Context): FileUtils {
        return FileUtils(context)
    }

    @Provides
    @Singleton
    fun provideRecipeBackupManager(
        @ApplicationContext context: Context,
        fileUtils: FileUtils,
    ): RecipeBackupManager {
        return RecipeBackupManager(context, fileUtils)
    }
}
