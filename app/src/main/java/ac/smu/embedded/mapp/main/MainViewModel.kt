package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asFlowable
import ac.smu.embedded.mapp.util.asLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Flowable

class MainViewModel : ViewModel() {
    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String> = _toastText

    private val db = FirebaseFirestore.getInstance()

    fun showToast(text: String) {
        _toastText.value = text
    }

    fun getTestLiveData(): LiveData<Resource<QuerySnapshot?>> {
        return db.collection("test").asLiveData()
    }

    fun getTestFlowable(): Flowable<Resource<QuerySnapshot?>> {
        return db.collection("test").asFlowable()
    }
}