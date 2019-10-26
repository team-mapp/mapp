package ac.smu.embedded.mapp.model

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val error: Throwable?
) {
    fun onSuccess(callback: (T?) -> Unit): Resource<T> {
        if (status == Status.SUCCESS) callback(data)
        return this
    }

    fun onError(callback: (Throwable) -> Unit): Resource<*> {
        if (status == Status.ERROR) callback(error!!)
        return this
    }

    fun onLoading(callback: () -> Unit): Resource<*> {
        if (status == Status.LOADING) callback()
        return this
    }

    fun <O> transform(transform: (T?) -> O): Resource<O> {
        return when (status) {
            Status.LOADING -> loading(transform(data))
            Status.SUCCESS -> success(transform(data))
            Status.ERROR -> error(error!!, null)
        }
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, error)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}