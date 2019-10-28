package ac.smu.embedded.mapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations

fun <I, O> LiveData<I>.map(mapFunction: (I) -> O): LiveData<O> =
    Transformations.map(this, mapFunction)

fun <I, O> LiveData<I>.switchMap(switchMapFunction: (I) -> LiveData<O>?): LiveData<O>? =
    Transformations.switchMap(this, switchMapFunction)

fun <I1, I2> LiveData<I1>.combineLatest(switchMapFunction: (I1?) -> LiveData<I2>?): LiveData<Pair<I1, I2>>? {
    val combineLiveData = switchMap(switchMapFunction) ?: return null
    return combineLatest(combineLiveData)
}

fun <I1, I2> LiveData<I1>.combineLatest(combineData: LiveData<I2>): LiveData<Pair<I1, I2>> {
    return MediatorLiveData<Pair<I1, I2>>().apply {
        var data1: I1? = null
        var data2: I2? = null

        addSource(this@combineLatest) {
            if (it == null && value != null) value = null
            data1 = it
            if (data1 != null && data2 != null) value = data1!! to data2!!
        }

        addSource(combineData) {
            if (it == null && value != null) value = null
            data2 = it
            if (data1 != null && data2 != null) value = data1!! to data2!!
        }
    }
}

fun <T> LiveData<T>.distinct(): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@distinct) {
            if (it != value) {
                value = it
            }
        }
    }
}

fun <T> LiveData<T>.filter(predicate: (T) -> Boolean): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@filter) {
            if (predicate(it)) {
                value = it
            }
        }
    }
}

fun <T> LiveData<T>.nonNull(): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@nonNull) {
            if (it != null) {
                value = it
            }
        }
    }
}

object LiveDataUtil {

    fun <T> zip(vararg liveDataList: LiveData<T>): LiveData<List<T>> {
        return zip(liveDataList.toList())
    }

    fun <T> zip(liveDataList: List<LiveData<T>>): LiveData<List<T>> {
        return MediatorLiveData<List<T>>().apply {
            val dataList = mutableListOf<T>()

            liveDataList.forEach {
                addSource(it) {
                    if (it != null) {
                        dataList.add(it)
                    }
                    if (dataList.size == liveDataList.size) {
                        value = dataList
                    }
                }
            }
        }
    }
}