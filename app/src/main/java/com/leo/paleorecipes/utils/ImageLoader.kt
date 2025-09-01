package com.leo.paleorecipes.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.imageLoader
import coil.load
import coil.request.Disposable
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transform.CircleCropTransformation
import com.leo.paleorecipes.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A wrapper around Coil for simplified image loading with common configurations.
 */
@Singleton
class ImageLoader @Inject constructor(private val context: Context) {

    /**
     * Loads an image from a URL into an ImageView with a placeholder and error image.
     *
     * @param url The URL of the image to load.
     * @param imageView The ImageView to load the image into.
     * @param placeholderResId The resource ID of the placeholder image to show while loading.
     * @param errorResId The resource ID of the error image to show if loading fails.
     * @param centerCrop Whether to apply center-crop transformation.
     * @param circleCrop Whether to apply circle-crop transformation.
     * @param onSuccess Callback when the image is successfully loaded.
     * @param onError Callback when there's an error loading the image.
     */
    fun loadImage(
        url: String?,
        imageView: ImageView,
        @DrawableRes placeholderResId: Int = R.drawable.ic_image_placeholder,
        @DrawableRes errorResId: Int = R.drawable.ic_broken_image,
        centerCrop: Boolean = true,
        circleCrop: Boolean = false,
        onSuccess: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
    ): Disposable {
        if (url.isNullOrEmpty()) {
            imageView.setImageResource(errorResId)
            onError?.invoke(IllegalArgumentException("URL cannot be null or empty"))
            // Return a completed disposable
            return object : Disposable {
                override val isDisposed: Boolean = true
                override fun dispose() {}
                override val job: Deferred<ImageResult> = CompletableDeferred<ImageResult>().apply { completeExceptionally(IllegalArgumentException("URL cannot be null or empty")) }
            }
        }

        return imageView.load(url) {
            crossfade(true)
            if (placeholderResId != 0) {
                placeholder(placeholderResId)
            }
            if (errorResId != 0) {
                error(errorResId)
            }
            if (centerCrop) {
                scale(coil.size.Scale.FILL)
            }
            if (circleCrop) {
                transformations(CircleCropTransformation())
            }
            listener(
                onSuccess = { _, _ -> onSuccess?.invoke() },
                onError = { _, result -> onError?.invoke(result.throwable) },
            )
        }
    }

    /**
     * Loads an image from a resource ID into an ImageView.
     *
     * @param resourceId The resource ID of the image to load.
     * @param imageView The ImageView to load the image into.
     * @param centerCrop Whether to apply center-crop transformation.
     * @param circleCrop Whether to apply circle-crop transformation.
     */
    fun loadImage(
        @DrawableRes resourceId: Int,
        imageView: ImageView,
        centerCrop: Boolean = true,
        circleCrop: Boolean = false,
    ) {
        imageView.load(resourceId) {
            if (centerCrop) {
                scale(coil.size.Scale.FILL)
            }
            if (circleCrop) {
                transformations(CircleCropTransformation())
            }
        }
    }

    /**
     * Loads an image from a URI into an ImageView.
     *
     * @param uri The URI of the image to load.
     * @param imageView The ImageView to load the image into.
     * @param placeholderResId The resource ID of the placeholder image.
     * @param errorResId The resource ID of the error image.
     * @param centerCrop Whether to apply center-crop transformation.
     * @param circleCrop Whether to apply circle-crop transformation.
     */
    fun loadImage(
        uri: Uri?,
        imageView: ImageView,
        @DrawableRes placeholderResId: Int = R.drawable.ic_image_placeholder,
        @DrawableRes errorResId: Int = R.drawable.ic_broken_image,
        centerCrop: Boolean = true,
        circleCrop: Boolean = false,
    ) {
        if (uri == null) {
            imageView.setImageResource(errorResId)
            return
        }

        imageView.load(uri) {
            crossfade(true)
            if (placeholderResId != 0) {
                placeholder(placeholderResId)
            }
            if (errorResId != 0) {
                error(errorResId)
            }
            if (centerCrop) {
                scale(coil.size.Scale.FILL)
            }
            if (circleCrop) {
                transformations(CircleCropTransformation())
            }
        }
    }

    /**
     * Loads an image as a Bitmap.
     *
     * @param url The URL of the image to load.
     * @param onSuccess Callback with the loaded Bitmap.
     * @param onError Callback when there's an error loading the image.
     */
    suspend fun loadImageAsBitmap(
        url: String?,
        onSuccess: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit = {},
    ) {
        if (url.isNullOrEmpty()) {
            onError(IllegalArgumentException("URL cannot be null or empty"))
            return
        }

        val request = ImageRequest.Builder(context)
            .data(url)
            .target(
                onSuccess = { result ->
                    (result as? android.graphics.drawable.BitmapDrawable)?.bitmap?.let {
                        onSuccess(it)
                    }
                },
                onError = { drawable ->
                    onError(Exception("Failed to load image: $drawable"))
                },
            )
            .build()
        context.imageLoader.enqueue(request)
    }

    /**
     * Clears the memory cache.
     */
    fun clearMemoryCache() {
        context.imageLoader.memoryCache?.clear()
    }

    /**
     * Clears the disk cache.
     */
    fun clearDiskCache() {
        context.imageLoader.diskCache?.clear()
    }

    /**
     * Clears the view so that the image is no longer loaded.
     */
    fun clearView(view: ImageView) {
        // Coil automatically handles view disposal, but you can cancel the request explicitly if needed
        // No direct equivalent to Glide's clear(view), but you can cancel the request if you have the disposable
    }

    companion object {
        /**
         * Preloads an image into the cache.
         *
         * @param context The context.
         * @param url The URL of the image to preload.
         */
        fun preloadImage(context: Context, url: String) {
            val request = ImageRequest.Builder(context)
                .data(url)
                .build()
            context.imageLoader.enqueue(request)
        }

        /**
         * Clears all Coil caches (memory and disk).
         *
         * @param context The context.
         */
        fun clearAllCaches(context: Context) {
            context.imageLoader.memoryCache?.clear()
            context.imageLoader.diskCache?.clear()
        }
    }
}
