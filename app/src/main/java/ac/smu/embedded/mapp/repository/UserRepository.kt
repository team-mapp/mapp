package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.NotificationToken
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.model.User.Companion.fromMap
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import kotlinx.coroutines.tasks.await

interface UserRepository {

    suspend fun signIn(credential: AuthCredential): User?

    fun signOut()

    suspend fun getUser(): User?

    suspend fun getUser(uid: String): User?

    fun updateUserProfile(displayName: String?, profileImage: String?)

    suspend fun deleteUser(): Boolean

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

    override suspend fun signIn(credential: AuthCredential): User? {
        val result = auth.signInWithCredential(credential).await()
        return if (result.user != null) {
            with(result.user!!) {
                val user = User(uid, displayName, photoUrl.toString())
                addUser(user)
                user
            }
        } else {
            null
        }
    }

    override fun signOut() = auth.signOut()

    override suspend fun getUser(): User? {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return getUser(currentUser.uid)
        }
        return null
    }

    override suspend fun getUser(uid: String): User? =
        db.collection(COLLECTION_USER_PATH)
            .document(uid)
            .get().await()
            .toObject(::fromMap)

    override fun updateUserProfile(displayName: String?, profileImage: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val updateMap = mutableMapOf<String, Any>()
            if (displayName != null) {
                updateMap[User.FIELD_DISPLAY_NAME] = displayName
            }
            if (profileImage != null) {
                updateMap[User.FIELD_PROFILE_IMAGE] = profileImage
            }
            if (updateMap.keys.size > 0) {
                db.collection(COLLECTION_USER_PATH)
                    .document(currentUser.uid)
                    .update(updateMap)
            }
        }
    }

    override suspend fun deleteUser(): Boolean {
        return auth.currentUser?.delete()?.await() != null
    }

    override suspend fun getNotificationToken(): InstanceIdResult? =
        FirebaseInstanceId.getInstance().instanceId.await()

    override fun addNotificationToken(token: String) {
        db.collection(COLLECTION_TOKEN_PATH)
            .document(token)
            .set(NotificationToken(auth.uid, Timestamp.now()))
    }

    private fun addUser(user: User) {
        db.collection(COLLECTION_USER_PATH)
            .document(user.uid!!)
            .set(user)
    }

    private fun createErrorData(message: String): LiveData<Resource<Void?>> {
        return MutableLiveData<Resource<Void?>>(Resource.error(Exception(message)))
    }
}