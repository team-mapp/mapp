package ac.smu.embedded.mapp.Intro

import ac.smu.embedded.mapp.repository.StorageRepository
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.UploadTask

class IntroViewModel (
    private val storageRepository: StorageRepository<UploadTask>
) : ViewModel() {


}