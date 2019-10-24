package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asFlowable
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.asSingle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Flowable
import io.reactivex.Single

class MainViewModel : ViewModel() {
    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String> = _toastText

    private val db = FirebaseFirestore.getInstance()

    fun showToast(text: String) {
        _toastText.value = text
    }

    fun getTestLiveData(): LiveData<Resource<QuerySnapshot?>> {
        return db.collection("test").get().asLiveData()
    }

    fun getTestSingle(): Single<QuerySnapshot?> {
        return db.collection("test").get().asSingle()
    }

    fun getTestSnapshotLiveData(): LiveData<Resource<QuerySnapshot?>> {
        return db.collection("test").asLiveData()
    }

    fun getTestSnapshotFlowable(): Flowable<Resource<QuerySnapshot?>> {
        return db.collection("test").asFlowable()
    }
}