package ac.smu.embedded.mapp.Intro

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.UploadTaskInfo
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.StorageRepository
import ac.smu.embedded.mapp.repository.UserRepository
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.UploadTask

class IntroViewModel (
    private val storageRepository: StorageRepository<UploadTask>,
    private val UserRepository :UserRepository
) : ViewModel() {

    fun upladImage(uri: Uri): LiveData<UploadTaskInfo<UploadTask>>? {
        val user = UserRepository.getUser()
        if (user != null) {
            return storageRepository.put("user/${user.uid}.jpg", uri)
        }
        return null
    }

}