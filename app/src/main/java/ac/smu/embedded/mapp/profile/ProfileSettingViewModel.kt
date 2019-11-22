package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileSettingViewModel(
    private val userRepository: UserRepository
) : StateViewModel() {

    val user: LiveData<User?> = useState()

    val validUsername: LiveData<Boolean> = useState(true)

    val profileUpdated: LiveData<Boolean> = useState(false)

    init {
        viewModelScope.launch {
            setState(user, userRepository.getUser())
        }
    }

    fun updateProfile(username: String?, profileImage: String?) {
        if (validProfile(username, profileImage)) {
            userRepository.updateUserProfile(username, profileImage)
            setState(profileUpdated, true)
        }
    }

    fun validProfile(username: String?, profileImage: String?): Boolean {
        var valid = true
        if (username != null && username.isEmpty()) {
            setState(validUsername, false)
            valid = false
        }
        return valid
    }
}