package com.leo.paleorecipes.utils

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Exception) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
    object Empty : ApiResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading"
            is Empty -> "Empty"
        }
    }
}

/**
 * `true` if [ApiResponse] is of type [ApiResponse.Success] & holds non-null [ApiResponse.Success.data].
 */
val ApiResponse<*>.succeeded: Boolean
    get() = this is ApiResponse.Success && data != null

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [ApiResponse.Success] or the original [ApiResponse.Error] if it is [ApiResponse.Error].
 */
inline fun <T, R> ApiResponse<T>.map(transform: (T) -> R): ApiResponse<R> {
    return when (this) {
        is ApiResponse.Success -> ApiResponse.Success(transform(data))
        is ApiResponse.Error -> this
        is ApiResponse.Loading -> this
        is ApiResponse.Empty -> this
    }
}

/**
 * Returns the encapsulated value if this instance represents [ApiResponse.Success] or `null` if it is [ApiResponse.Error].
 */
fun <T> ApiResponse<T>.getOrNull(): T? = when (this) {
    is ApiResponse.Success -> data
    else -> null
}

/**
 * Returns the encapsulated value if this instance represents [ApiResponse.Success] or the [default] value if it is [ApiResponse.Error].
 */
fun <T> ApiResponse<T>.getOrDefault(default: T): T = when (this) {
    is ApiResponse.Success -> data
    else -> default
}

/**
 * Returns the encapsulated value if this instance represents [ApiResponse.Success] or throws the encapsulated exception if it is [ApiResponse.Error].
 */
fun <T> ApiResponse<T>.getOrThrow(): T = when (this) {
    is ApiResponse.Success -> data
    is ApiResponse.Error -> throw exception
    is ApiResponse.Loading -> throw IllegalStateException("Result is loading")
    is ApiResponse.Empty -> throw NoSuchElementException("Result is empty")
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [ApiResponse.Success] or the original [ApiResponse.Error] if it is [ApiResponse.Error].
 */
inline fun <T, R> ApiResponse<T>.flatMap(transform: (T) -> ApiResponse<R>): ApiResponse<R> {
    return when (this) {
        is ApiResponse.Success -> transform(data)
        is ApiResponse.Error -> this
        is ApiResponse.Loading -> this
        is ApiResponse.Empty -> this
    }
}
