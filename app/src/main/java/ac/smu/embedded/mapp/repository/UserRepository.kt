package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

interface UserRepository {

    fun signIn(credential: AuthCredential): LiveData<Resource<User?>>

    suspend fun signInAwait(credential: AuthCredential): User?

    fun signOut()

    fun getUser(): User?

    fun updateUserProfile(displayName: String?, profileImage: String?): LiveData<Resource<Void?>>

    fun deleteUser(): LiveData<Resource<Void?>>

}

class UserRepositoryImpl(private val auth: FirebaseAuth) : UserRepository {
    override fun signIn(credential: AuthCredential): LiveData<Resource<User?>> =
        auth.signInWithCredential(credential).asLiveData().map { resource ->
            resource.transform {
                if (it != null) {
                    User(it.user?.uid, it.user?.displayName, it.user?.photoUrl.toString())
                } else {
                    null
                }
            }
        }

    override suspend fun signInAwait(credential: AuthCredential): User? {
        val result = auth.signInWithCredential(credential).await()
        return if (result.user != null) {
            with(result.user!!) {
                User(uid, displayName, photoUrl.toString())
            }
        } else {
            null
        }
    }

    override fun signOut() = auth.signOut()

    override fun getUser(): User? {
        val user = auth.currentUser
        return if (user != null) {
            User(user.uid, user.displayName, user.photoUrl.toString())
        } else {
            null
        }
    }

    override fun updateUserProfile(
        displayName: String?,
        profileImage: String?
    ): LiveData<Resource<Void?>> {
        val builder = UserProfileChangeRequest.Builder()
        if (displayName != null) {
            builder.setDisplayName(displayName)
        }
        if (profileImage != null) {
            builder.setPhotoUri(Uri.parse(profileImage))
        }

        val user = auth.currentUser
        return user?.updateProfile(builder.build())?.asLiveData() ?: createErrorData("Unknown user")
    }

    override fun deleteUser(): LiveData<Resource<Void?>> {
        return auth.currentUser?.delete()?.asLiveData() ?: createErrorData("Unknown user")
    }

    private fun createErrorData(message: String): LiveData<Resource<Void?>> {
        return MutableLiveData<Resource<Void?>>(Resource.error(Exception(message)))
    }
}