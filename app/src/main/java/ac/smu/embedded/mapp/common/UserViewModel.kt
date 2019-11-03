package ac.smu.embedded.mapp.common

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.UserRepository
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _startActivityForResult = MutableLiveData<Pair<Intent, Int>>()
    val startActivityForResult: LiveData<Pair<Intent, Int>> = _startActivityForResult

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    private lateinit var userObserver: Observer<Resource<*>>

    init {
        updateUser()
    }

    fun signIn(credential: AuthCredential) = userRepository.signIn(credential)

    fun signInWithGoogle(context: Context, clientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()

        val signInIntent = GoogleSignIn.getClient(context, gso).signInIntent
        if (startActivityForResult.hasObservers()) {
            _startActivityForResult.value = Pair(signInIntent, RC_SIGN_IN)
        } else {
            throw UnsupportedOperationException("Not registered startActivityForResult observer")
        }
    }

    fun signOut() {
        userRepository.signOut()
        updateUser()
    }

    fun updateUserProfile(displayName: String?, profileImage: String?) {
        val updateUserProfile = userRepository.updateUserProfile(displayName, profileImage)
        userObserver = Observer {
            it.onSuccess { updateUser() }
            updateUserProfile.removeObserver(userObserver)
        }
    }

    fun deleteUser() {
        val deleteUser = userRepository.deleteUser()
        userObserver = Observer {
            it.onSuccess { updateUser() }
            deleteUser.removeObserver(userObserver)
        }
        deleteUser.observeForever(userObserver)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                val signIn = signIn(credential)
                userObserver = Observer {
                    it.onSuccess { updateUser() }
                    signIn.removeObserver(userObserver)
                }
                signIn.observeForever(userObserver)
            } catch (e: ApiException) {
                Log.e(TAG, "Error occurred", e)
            }
        }
    }

    private fun updateUser() {
        _userData.value = userRepository.getUser()
    }

    companion object {
        private const val TAG = "UserViewModel"
        private const val RC_SIGN_IN = 100
    }
}