package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.model.UploadTaskInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.UploadTask

fun UploadTask.asLiveData(): LiveData<UploadTaskInfo<UploadTask>> {
    val liveData = MutableLiveData<UploadTaskInfo<UploadTask>>()
    addOnProgressListener {
        liveData.value = UploadTaskInfo.progress(this, it.bytesTransferred, it.totalByteCount)
    }
    addOnSuccessListener {
        liveData.value = UploadTaskInfo.success(this, it.bytesTransferred, it.totalByteCount)
    }
    addOnCompleteListener {
        liveData.value = UploadTaskInfo.complete(this)
    }
    addOnPausedListener {
        liveData.value = UploadTaskInfo.paused(this, it.bytesTransferred, it.totalByteCount)
    }
    addOnFailureListener {
        liveData.value = UploadTaskInfo.error(this, it)
    }
    addOnCanceledListener {
        liveData.value = UploadTaskInfo.error(this, Error("Task canceled"))
    }
    return liveData
}