package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.model.Resource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Firestore를 이용할때 [Task] 결과에 대한 콜백을 [LiveData]로 Wrapping 해주는 Extension 함수
 * [Task]에 대한 결과는 [Resource] 클래스를 통해 전달됩니다.
 *
 * - 예제
 * ```kotlin
 * val db = FirebaseFirestore.getInstance()
 * db.collection("test").get().asLiveData()
 * ```
 *
 * @return Task 콜백에 대해 Wrapping 된 [LiveData]
 */
fun <TResult> Task<TResult>.asLiveData(): LiveData<Resource<TResult?>> {
    val liveData = MutableLiveData<Resource<TResult?>>()
    liveData.value = Resource.loading()
    addOnSuccessListener {
        liveData.value = Resource.success(it)
    }
    addOnFailureListener {
        liveData.value = Resource.error(it)
    }
    addOnCanceledListener {
        liveData.value = Resource.error(Error("Task canceled"))
    }
    return liveData
}

/**
 * Firestore에서 주어지는 [DocumentReference] 결과에 대한 SnapshotListener를 [LiveData]로 Wrapping 해주는 Extension 함수
 * 리스너로부터 나오는 결과는 [Resource] 클래스를 통해 전달됩니다.
 *
 * - 예제
 * ```kotlin
 * val db = FirebaseFirestore.getInstance()
 * db.collection("test").document("abc").asLiveData()
 * ```
 *
 * @return SnapshotListener를 Wrapping 한 [LiveData]
 */
fun DocumentReference.asLiveData(): LiveData<Resource<DocumentSnapshot?>> {
    val liveData = MutableLiveData<Resource<DocumentSnapshot?>>()
    liveData.value = Resource.loading()
    addSnapshotListener { snapshot, e ->
        if (e == null) {
            liveData.value = Resource.success(snapshot)
        } else {
            liveData.value = Resource.error(e)
        }
    }
    return liveData
}

/**
 * Firestore에서 주어지는 [Query] 결과에 대한 SnapshotListener를 [LiveData]로 Wrapping 해주는 Extension 함수
 * 리스너로부터 나오는 결과는 [Resource] 클래스를 통해 전달됩니다.
 *
 * - 예제
 * ```kotlin
 * val db = FirebaseFirestore.getInstance()
 * db.collection("test").asLiveData()
 * db.collection("test").whereEqualTo("name", "abc").asLiveData()
 * ```
 *
 * @return SnapshotListener를 Wrapping 한 [LiveData]
 */
fun Query.asLiveData(): LiveData<Resource<QuerySnapshot?>> {
    val liveData = MutableLiveData<Resource<QuerySnapshot?>>()
    liveData.value = Resource.loading()
    addSnapshotListener { snapshot, e ->
        if (e == null) {
            liveData.value = Resource.success(snapshot)
        } else {
            liveData.value = Resource.error(e)
        }
    }
    return liveData
}

@ExperimentalCoroutinesApi
fun Query.asFlow(): Flow<QuerySnapshot?> = callbackFlow {
    val listener = addSnapshotListener { snapshot, exception ->
        if (exception != null) {
            cancel(exception.toString(), exception)
        } else {
            offer(snapshot)
        }
    }
    awaitClose { listener.remove() }
}

fun <T> QuerySnapshot.toObject(transform: (String, Map<String, Any>) -> T): List<T>? {
    val list = documents
        .filter { it.data != null }
        .map { transform(it.id, it.data!!) }
    return if (list.isNotEmpty()) list else null
}

fun <T> DocumentSnapshot.toObject(transform: (String, Map<String, Any>) -> T): T? {
    return if (data != null) transform(id, data!!) else null
}