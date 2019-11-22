package ac.smu.embedded.mapp.common

import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.UserRepository
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    init {
        updateUser()
    }

    fun signIn(credential: AuthCredential) = viewModelScope.launch {
        _userData.value = userRepository.signIn(credential)
        val idResult = userRepository.getNotificationToken()
        if (idResult != null) {
            userRepository.addNotificationToken(idResult.token)
        }
    }

    fun signInWithGoogle(activity: Activity, clientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()

        val signInIntent = GoogleSignIn.getClient(activity, gso).signInIntent
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    fun signOut() {
        userRepository.signOut()
        updateUser()
    }

    fun updateUserProfile(displayName: String?, profileImage: String?) = viewModelScope.launch {
        userRepository.updateUserProfile(displayName, profileImage)
        updateUser()
    }

    fun deleteUser() = viewModelScope.launch {
        userRepository.deleteUser()
        updateUser()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                signIn(credential)
            } catch (e: ApiException) {
                Log.e(TAG, "Error occurred", e)
            }
        }
    }

    private fun updateUser() = viewModelScope.launch {
        _userData.value = userRepository.getUser()
    }

    companion object {
        private const val TAG = "UserViewModel"
        private const val RC_GOOGLE_SIGN_IN = 100
    }
}