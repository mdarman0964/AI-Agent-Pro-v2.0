package com.aiagent.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int = 0) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.onSuccess(action: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) action(data)
    return this
}

inline fun <T> ApiResult<T>.onError(action: (String) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) action(message)
    return this
}

inline fun <T> ApiResult<T>.onLoading(action: () -> Unit): ApiResult<T> {
    if (this is ApiResult.Loading) action()
    return this
}
