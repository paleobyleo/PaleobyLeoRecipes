package com.leo.paleorecipes.utils

/**
 * A generic sealed class that represents a resource (like a network request or database operation)
 * that can be in one of three states: Success, Error, or Loading.
 *
 * @param T The type of the data that will be wrapped by this Resource.
 * @property data The actual data if the operation was successful, or null otherwise.
 * @property message A message describing the status of the operation, typically used for errors.
 * @property throwable An optional throwable that was thrown during the operation.
 */
sealed class Resource<T> @JvmOverloads constructor(
    val data: T? = null,
    val message: String? = null,
    val throwable: Throwable? = null,
) {
    /**
     * Represents a successful operation with the [data] result.
     *
     * @param data The successful result data.
     */
    class Success<T>(data: T) : Resource<T>(data = data)

    /**
     * Represents a loading state with an optional [data] if available.
     *
     * @param data Optional cached data that might be available while loading.
     */
    class Loading<T>(data: T? = null) : Resource<T>(data = data)

    /**
     * Represents a failed operation with an optional [message] and [throwable].
     *
     * @param message A message describing the error.
     * @param throwable The exception that caused the error, if any.
     * @param data The last known data before the error occurred, if any.
     */
    class Error<T>(
        message: String,
        throwable: Throwable? = null,
        data: T? = null,
    ) : Resource<T>(data = data, message = message, throwable = throwable)

    /**
     * Returns `true` if this instance represents a successful result.
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Returns `true` if this instance represents a loading state.
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Returns `true` if this instance represents an error.
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Returns the data if this instance is [Success], or `null` otherwise.
     */
    fun getOrNull(): T? = if (this is Success) data else null

    /**
     * Returns the data if this instance is [Success], or [default] otherwise.
     *
     * @param default The default value to return if the resource is not a success.
     */
    fun getOrDefault(default: T): T = if (this is Success) data ?: default else default

    /**
     * Returns the data if this instance is [Success], or throws an exception otherwise.
     *
     * @throws IllegalStateException if the resource is not a success.
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data ?: throw IllegalStateException("Success state contains null data")
        is Error -> throw throwable ?: IllegalStateException(message ?: "Unknown error occurred")
        is Loading -> throw IllegalStateException("Cannot get value from loading state")
    }

    /**
     * Maps the data of a [Success] result using the given [transform] function.
     *
     * @param transform A function that transforms the success data.
     * @return A new [Resource] with the transformed data.
     */
    fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Success -> try {
            Success(transform(data ?: throw IllegalStateException("Cannot transform null data")))
        } catch (e: Exception) {
            Error(e.message ?: "Error during transformation", e, null)
        }
        is Loading -> Loading(data?.let(transform))
        is Error -> Error(message ?: "An error occurred", throwable, data?.let(transform))
    }

    /**
     * Maps the data of a [Success] result using the given [transform] function that returns a [Resource].
     *
     * @param transform A function that transforms the success data into another [Resource].
     * @return The transformed [Resource].
     */
    fun <R> flatMap(transform: (T) -> Resource<R>): Resource<R> = when (this) {
        is Success -> try {
            transform(data ?: throw IllegalStateException("Cannot transform null data"))
        } catch (e: Exception) {
            Error(e.message ?: "Error during transformation", e, null)
        }
        is Loading -> Loading()
        is Error -> Error(message ?: "An error occurred", throwable, null)
    }

    /**
     * Executes the given [action] if this instance is [Success].
     *
     * @param action The action to execute with the success data.
     * @return This instance for method chaining.
     */
    fun onSuccess(action: (T) -> Unit): Resource<T> = apply {
        if (this is Success && data != null) action(data)
    }

    /**
     * Executes the given [action] if this instance is [Error].
     *
     * @param action The action to execute with the error message and throwable.
     * @return This instance for method chaining.
     */
    fun onError(action: (message: String?, throwable: Throwable?) -> Unit): Resource<T> = apply {
        if (this is Error) action(message, throwable)
    }

    /**
     * Executes the given [action] if this instance is [Loading].
     *
     * @param action The action to execute with the loading data.
     * @return This instance for method chaining.
     */
    fun onLoading(action: (T?) -> Unit): Resource<T> = apply {
        if (this is Loading) action(data)
    }

    companion object {
        /**
         * Creates a [Resource.Success] with the given [data].
         *
         * @param data The successful result data.
         * @return A new [Success] instance.
         */
        fun <T> success(data: T): Resource<T> = Success(data)

        /**
         * Creates a [Resource.Loading] with the given optional [data].
         *
         * @param data Optional cached data that might be available while loading.
         * @return A new [Loading] instance.
         */
        fun <T> loading(data: T? = null): Resource<T> = Loading(data)

        /**
         * Creates a [Resource.Error] with the given [message] and optional [throwable] and [data].
         *
         * @param message A message describing the error.
         * @param throwable The exception that caused the error, if any.
         * @param data The last known data before the error occurred, if any.
         * @return A new [Error] instance.
         */
        fun <T> error(message: String, throwable: Throwable? = null, data: T? = null): Resource<T> =
            Error(message, throwable, data)
    }
}
