package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.StorageRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.StateViewModel
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileSettingViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository<UploadTask>
) : StateViewModel() {

    val user: LiveData<User?> = useState()

    val validUsername: LiveData<Boolean> = useState(true)

    val profileUpdated: LiveData<Boolean> = useState(false)

    val profileImage: LiveData<String?> = useState(null)

    val showProgress: LiveData<Boolean> = useState(true)

    init {
        viewModelScope.launch {
            setState(user, userRepository.getUser())
            setState(showProgress, false)
        }
    }

    fun updateProfile(username: String?) {
        if (validProfile(username)) {
            userRepository.updateUserProfile(username, profileImage.value)
            setState(profileUpdated, true)
        }
    }

    fun validProfile(username: String?): Boolean {
        var valid = true
        if (username != null && username.isEmpty()) {
            setState(validUsername, false)
            valid = false
        }
        return valid
    }

    fun uploadImage(uri: Uri) = viewModelScope.launch {
        val user = userRepository.getUserWithoutProfile()
        if (user != null) {
            val path = "users/${user}_${System.currentTimeMillis()}.jpg"
            setState(showProgress, true)
            storageRepository.put(path, uri).asFlow().collect {
                it.onComplete {
                    setState(profileImage, path)
                    setState(showProgress, false)
                }
            }
        }
    }
}