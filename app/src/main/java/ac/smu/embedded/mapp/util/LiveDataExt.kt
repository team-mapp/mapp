package ac.smu.embedded.mapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations

/**
 * [LiveData] 내 데이터를 변형하여 데이터를 내보냅니다.
 *
 * ```kotlin
 * val liveData = MutableLiveData<String>("Hello")
 * val mappedLiveData = liveData.map { it + " World" }
 * mappedLiveData.value // "Hello World"
 * ```
 *
 * @param mapFunction Transformations.map에 제공되는 람다 함수
 * @return 데이터가 변형된 [LiveData]
 */
fun <I, O> LiveData<I>.map(mapFunction: (I) -> O): LiveData<O> =
    Transformations.map(this, mapFunction)

/**
 * 현재 [LiveData] 대신 다른 [LiveData] 으로 변환합니다.
 * 주로 데이터를 활용하여 다른 [LiveData] 로 변환할떄 사용합니다.
 *
 * ```kotlin
 * fun newLiveData(data: String): LiveData<String>
 *     = MutableLiveData<String>(data + " World")
 *
 * val liveData = MutableLiveData<String>("Hello")
 * val switchMappedLiveData = liveData.switchMap {
 *  newLiveData(it)
 * }
 * switchMappedLiveData.value // "Hello World"
 * ```
 *
 * @param switchMapFunction Transformations.switchMap에 제공되는 람다 함수
 * @return switchMapFunction에 의해 변형된 [LiveData]
 */
fun <I, O> LiveData<I>.switchMap(switchMapFunction: (I) -> LiveData<O>?): LiveData<O>? =
    Transformations.switchMap(this, switchMapFunction)

/**
 * 다른 [LiveData]와 결합하여 두 [LiveData]의 데이터를 가져오도록 만들어주는 함수입니다
 * 이 함수는 현재 [LiveData]의 데이터를 받고 그 데이터를 활용하여 가져온 다른 [LiveData]와 결합하기 위해
 * [switchMap] 기능이 들어간 함수입니다.
 *
 * ```kotlin
 * fun newLiveData(data: String): LiveData<String>
 *     = MutableLiveData<String>(data + " World")
 *
 * val liveData = MutableLiveData<String>("Hello")
 * val combineLatestData = liveData.combineLatest {
 *  newLiveData(it)
 * }
 *
 * combineLatestData.value = Pair("Hello", "Hello World")
 * ```
 *
 * @param switchMapFunction Transformations.switchMap에 제공되는 람다 함수
 * @return switchMapFunction에 의해 변형된 [LiveData]와 결합된 [LiveData]로 값은 [Pair]을 통해 제공됩니다.
 */
fun <I1, I2> LiveData<I1>.combineLatest(switchMapFunction: (I1?) -> LiveData<I2>?): LiveData<Pair<I1, I2>>? {
    val combineLiveData = switchMap(switchMapFunction) ?: return null
    return combineLatest(combineLiveData)
}

/**
 * 다른 [LiveData]와 결합하여 두 [LiveData]의 데이터를 가져오도록 만들어주는 함수입니다
 *
 * ```kotlin
 * fun newLiveData(data: String): LiveData<String>
 *     = MutableLiveData<String>(data + " World")
 *
 * val liveData = MutableLiveData<String>("Hello")
 * val switchMappedLiveData = liveData.switchMap {
 *  newLiveData(it)
 * }
 * val combineLatestData = liveData.combineLatest(switchMappedLiveData)
 * combineLatestData.value = Pair("Hello", "Hello World")
 * ```
 *
 * @param combineData 결합하기 위한 다른 [LiveData]
 * @return switchMapFunction에 의해 변형된 [LiveData]와 결합된 [LiveData]로 값은 [Pair]을 통해 제공됩니다.
 */
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

/**
 * 연달아 나오는 종복 데이터를 거른 데이터만 내보내도록 변형해주는 함수입니다.
 *
 * ```kotlin
 * val liveData = MutableLiveData<String>("Hello", "Hello")
 * liveData.observe(this, Observer {
 *  print(it + " ")
 * })
 *
 * >>> Hello Hello
 *
 * liveData.distinct().observe(this, Observer {
 *  print(it + " ")
 * }
 *
 * >>> Hello
 * ```
 *
 * @return 중복된 데이터를 걸러낸 [LiveData]
 */
fun <T> LiveData<T>.distinct(): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@distinct) {
            if (it != value) {
                value = it
            }
        }
    }
}

/**
 * 조건에 맞는 데이터만 내보내도록 변형해주는 함수입니다.
 *
 * ```kotlin
 * val liveData = MutableLiveData<String>("ABC", "DEFG")
 * liveData.observe(this, Observer {
 *  print(it + " ")
 * })
 *
 * >>> ABC DEFG
 *
 * liveData.filter { it.length < 4 }.observe(this, Observer {
 *  print(it + " ")
 * }
 *
 * >>> ABC
 * ```
 *
 * @param predicate 데이터를 내보내는 조건을 제공하는 람다 함수
 * @return 조건에 맞는 데이터만 내보내는 [LiveData]
 */
fun <T> LiveData<T>.filter(predicate: (T) -> Boolean): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@filter) {
            if (predicate(it)) {
                value = it
            }
        }
    }
}

/**
 * null이 아닌 데이터만 내보내도록 변형해주는 함수입니다.
 *
 * ```kotlin
 * val liveData = MutableLiveData<String?>(null, "ABC", null)
 * liveData.observe(this, Observer {
 *  print(it + " ")
 * })
 *
 * >>> null ABC null
 *
 * liveData.nonNull().observe(this, Observer {
 *  print(it + " ")
 * }
 *
 * >>> ABC
 * ```
 *
 * @return null이 아닌 데이터만 내보내는 [LiveData]
 */
fun <T> LiveData<T>.nonNull(): LiveData<T> = filter { it != null }

object LiveDataUtil {

    fun <T> zip(vararg liveDataList: LiveData<T>): LiveData<List<T>> {
        return zip(liveDataList.toList())
    }

    /**
     * 여러 [LiveData]를 결합하여 각 데이터들을 [List]로 내보내주는 함수입니다.
     * 이때 각 [LiveData]는 데이터 타입이 같아야 합니다.
     *
     * ```kotlin
     * val liveData1 = MutableLiveData<String>("A")
     * val liveData2 = MutableLiveData<String>("B")
     * val liveData3 = MutableLiveData<String>("C")
     *
     * LiveDataUtil.zip(listOf(liveData1, liveData2, liveData3).observe(this, Observer {
     *  println(it)
     * })
     *
     * >>> ["A, "B", "C"]
     * ```
     *
     * @param liveDataList 결합할 [LiveData] 리스트
     * @return [LiveData]들을 결합한 [LiveData]
     */
    fun <T> zip(liveDataList: List<LiveData<T>>): LiveData<List<T>> {
        return MediatorLiveData<List<T>>().apply {
            val dataList = mutableListOf<T>()

            liveDataList.forEach { liveData ->
                addSource(liveData) {
                    if (it != null) {
                        dataList.add(it)
                    }
                    if (dataList.size == liveDataList.size) {
                        value = dataList
                        dataList.clear()
                    }
                }
            }
        }
    }
}