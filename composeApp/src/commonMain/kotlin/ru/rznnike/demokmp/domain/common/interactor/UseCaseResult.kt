package ru.rznnike.demokmp.domain.common.interactor

class UseCaseResult<out R>(
    val data: R? = null,
    val error: Exception? = null
) {
    override fun toString(): String {
        return error?.let {
            "Error[exception=$error]"
        } ?: run {
            "Success[data=$data]"
        }
    }

    suspend fun process(
        onSuccessCallback: suspend (R) -> Unit,
        onErrorCallback: (suspend (Exception) -> Unit)? = null
    ): UseCaseResult<R> {
        error?.let {
            onErrorCallback?.invoke(it)
        } ?: run {
            data?.let {
                onSuccessCallback(it)
            } ?: run {
                onErrorCallback?.invoke(
                    Exception("Unexpected exception in UseCaseResult: data=null and error=null")
                )
            }
        }

        return this
    }
}