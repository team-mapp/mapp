package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.NotificationToken
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.coroutines.tasks.await

interface UserRepository {

    fun signIn(credential: AuthCredential): LiveData<Resource<User?>>

    suspend fun signInAwait(credential: AuthCredential): User?

    fun signOut()

    fun getUser(): User?

    fun updateUserProfile(displayName: String?, profileImage: String?): LiveData<Resource<Void?>>

    suspend fun updateUserProfileAwait(displayName: String?, profileImage: String?): Boolean

    fun deleteUser(): LiveData<Resource<Void?>>

    suspend fun deleteUserAwait(): Boolean

    suspend fun getNotificationToken(): InstanceIdResult?

    fun addNotificationToken(token: String)

}

class UserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) :
    UserRepository {

    companion object {
        const val COLLECTION_USER_PATH = "users"
        const val COLLECTION_TOKEN_PATH = "tokens"
    }

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

    override suspend fun updateUserProfileAwait(
        displayName: String?,
        profileImage: String?
    ): Boolean {
        val builder = UserProfileChangeRequest.Builder()
        if (displayName != null) {
            builder.setDisplayName(displayName)
        }
        if (profileImage != null) {
            builder.setPhotoUri(Uri.parse(profileImage))
        }

        val user = auth.currentUser
        return user?.updateProfile(builder.build())?.await() != null
    }

    override fun deleteUser(): LiveData<Resource<Void?>> {
        return auth.currentUser?.delete()?.asLiveData() ?: createErrorData("Unknown user")
    }

    override suspend fun deleteUserAwait(): Boolean {
        return auth.currentUser?.delete()?.await() != null
    }

    override suspend fun getNotificationToken(): InstanceIdResult? =
        FirebaseInstanceId.getInstance().instanceId.await()

    override fun addNotificationToken(token: String) {
        db.collection(COLLECTION_TOKEN_PATH)
            .document(token)
            .set(NotificationToken(auth.uid, Timestamp.now()))
    }

    private fun createErrorData(message: String): LiveData<Resource<Void?>> {
        return MutableLiveData<Resource<Void?>>(Resource.error(Exception(message)))
    }
}