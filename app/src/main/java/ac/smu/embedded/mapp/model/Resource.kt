package ac.smu.embedded.mapp.model

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val error: Throwable?
) {
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

fun <I, O> Resource<I>.transform(transform: (I?) -> O): Resource<O> {
    return when (status) {
        Status.LOADING -> Resource.loading(transform(data))
        Status.SUCCESS -> Resource.success(transform(data))
        Status.ERROR -> Resource.error(error!!, null)
    }
}