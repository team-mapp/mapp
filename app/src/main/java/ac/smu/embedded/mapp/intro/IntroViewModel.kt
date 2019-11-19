package ac.smu.embedded.mapp.intro

import ac.smu.embedded.mapp.repository.StorageRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.StateViewModel
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class IntroViewModel(
    private val storageRepository: StorageRepository<UploadTask>,
    private val UserRepository: UserRepository
) : StateViewModel() {

    val uploadCompleted = useState(false)

    fun upladImage(uri: Uri) = viewModelScope.launch {
        val user = UserRepository.getUser()
        if (user != null) {
            storageRepository.put("users/${user.uid}.jpg", uri).asFlow().collect {
                it.onComplete { setState(uploadCompleted, true) }
            }
        }
    }
}