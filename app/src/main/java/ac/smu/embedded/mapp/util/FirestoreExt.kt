package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.model.Resource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

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
    liveData.value = Resource.loading(null)
    addOnSuccessListener {
        liveData.value = Resource.success(it)
    }
    addOnFailureListener {
        liveData.value = Resource.error(it, null)
    }
    addOnCanceledListener {
        liveData.value = Resource.error(Error("Task canceled"), null)
    }
    return liveData
}

/**
 * Firestore를 이용할때 [Task] 결과에 대한 콜백을 [Flowable]로 Wrapping 해주는 Extension 함수
 * [Task]가 Failure, Canceled 될 경우 [Flowable.doOnError]로 핸들링할 수 있습니다.
 *
 * - 예제
 * ```kotlin
 * val db = FirebaseFirestore.getInstance()
 * db.collection("test").get().asFlowable()
 * ```
 *
 * @return Task 콜백에 대해 Wrapping 된 [Flowable]
 */
fun <TResult> Task<TResult>.asFlowable(): Flowable<TResult?> {
    val subject = PublishSubject.create<TResult?>()
    addOnSuccessListener {
        subject.onNext(it)
    }
    addOnFailureListener {
        subject.onError(it)
    }
    addOnCanceledListener {
        subject.onError(Error("Task canceled"))
    }
    return subject.toFlowable(BackpressureStrategy.LATEST)
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
    liveData.value = Resource.loading(null)
    addSnapshotListener { snapshot, e ->
        if (e == null) {
            liveData.value = Resource.success(snapshot)
        } else {
            liveData.value = Resource.error(e, null)
        }
    }
    return liveData
}

/**
 * Firestore에서 주어지는 [DocumentReference] 결과에 대한 SnapshotListener를 [Flowable]로 Wrapping 해주는 Extension 함수
 * 리스너로부터 나오는 결과는 [Resource] 클래스를 통해 전달됩니다.
 *
 * - 예제
 * ```kotlin
 * val db = FirebaseFirestore.getInstance()
 * db.collection("test").document("abc").asFlowable()
 * ```
 *
 * @return SnapshotListener를 Wrapping 한 [Flowable]
 */
fun DocumentReference.asFlowable(): Flowable<Resource<DocumentSnapshot?>> {
    val subject = BehaviorSubject.create<Resource<DocumentSnapshot?>>()
    subject.onNext(Resource.loading(null))
    addSnapshotListener { snapshot, e ->
        if (e == null) {
            subject.onNext(Resource.success(snapshot))
        } else {
            subject.onNext(Resource.error(e, null))
        }
    }
    return subject.toFlowable(BackpressureStrategy.LATEST)
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
    liveData.value = Resource.loading(null)
    addSnapshotListener { snapshot, e ->
        if (e == null) {
            liveData.value = Resource.success(snapshot)
        } else {
            liveData.value = Resource.error(e, null)
        }
    }
    return liveData
}

/**
 * Firestore에서 주어지는 [Query] 결과에 대한 SnapshotListener를 [Flowable]로 Wrapping 해주는 Extension 함수
 * 리스너로부터 나오는 결과는 [Resource] 클래스를 통해 전달됩니다.
 *
 * - 예제
 * ```kotlin
 * val db = FirebaseFirestore.getInstance()
 * db.collection("test").asFlowable()
 * db.collection("test").whereEqualTo("name", "abc").asFlowable()
 * ```
 *
 * @return SnapshotListener를 Wrapping 한 [Flowable]
 */
fun Query.asFlowable(): Flowable<Resource<QuerySnapshot?>> {
    val subject = BehaviorSubject.create<Resource<QuerySnapshot?>>()
    subject.onNext(Resource.loading(null))
    addSnapshotListener { snapshot, e ->
        if (e == null) {
            subject.onNext(Resource.success(snapshot))
        } else {
            subject.onNext(Resource.error(e, null))
        }
    }
    return subject.toFlowable(BackpressureStrategy.LATEST)
}