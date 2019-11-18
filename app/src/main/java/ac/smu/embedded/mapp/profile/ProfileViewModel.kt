package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.repository.StorageRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.UploadTask

class ProfileViewModel (
    private val storageRepository: StorageRepository<UploadTask>
) : ViewModel() {
    fun uploadImage(uri: Uri, userId: String) =
        storageRepository.put(userId + ".jpg", uri)
}