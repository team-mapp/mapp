package ac.smu.embedded.mapp.model

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val error: Throwable?
) {
    /**
     * [Resource]의 상태가 [Status.SUCCESS]일때만 수행되는 콜백 함수
     *
     * ```kotlin
     * val resource = ...
     * resource.onSuccess {
     *   println("Success!")
     * }
     * ```
     *
     * @param callback
     * @return [Resource]
     */
    fun onSuccess(callback: (T?) -> Unit): Resource<T> {
        if (status == Status.SUCCESS) callback(data)
        return this
    }

    /**
     * [Resource]의 상태가 [Status.ERROR]일때만 수행되는 콜백 함수
     *
     * ```kotlin
     * val resource = ...
     * resource.onError {
     *   println("Error")
     * }
     * ```
     *
     * @param callback
     * @return [Resource]
     */
    fun onError(callback: (Throwable) -> Unit): Resource<*> {
        if (status == Status.ERROR) callback(error!!)
        return this
    }

    /**
     * [Resource]의 상태가 [Status.LOADING]일때만 수행되는 콜백 함수
     *
     * ```kotlin
     * val resource = ...
     * resource.onLoading {
     *   println("Loading...")
     * }
     * ```
     *
     * @param callback
     * @return [Resource]
     */
    fun onLoading(callback: () -> Unit): Resource<*> {
        if (status == Status.LOADING) callback()
        return this
    }

    /**
     * [Resource] 내 데이터를 람다식을 통해 변형하여 새로운 [Resource] 만들어주는 함수
     *
     * ```kotlin
     * val resource = Resource.success("Hello")
     * resource.transform {
     *   it + " World"
     * }
     * println(resource.data)
     *
     * >>> Hello World
     * ```
     *
     * @param callback
     * @return [Resource]
     */
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