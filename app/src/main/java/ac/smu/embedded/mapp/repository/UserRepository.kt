package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.NotificationToken
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.model.User.Companion.fromMap
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
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

    fun signIn(credential: AuthCredential): LiveData<Resource<User?>>

    suspend fun signInAwait(credential: AuthCredential): User?

    fun signOut()

    suspend fun getUser(): User?

    suspend fun getUser(uid: String): User?

    fun updateUserProfile(displayName: String?, profileImage: String?)

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
                    with(it.user!!) {
                        val user = User(uid, displayName, photoUrl.toString())
                        addUser(user)
                        user
                    }
                } else {
                    null
                }
            }
        }

    override suspend fun signInAwait(credential: AuthCredential): User? {
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
        val uid = auth.uid
        if (uid != null) {
            return getUser(uid)
        }
        return null
    }

    override suspend fun getUser(uid: String): User? =
        db.collection(COLLECTION_USER_PATH)
            .document(uid)
            .get().await()
            .toObject(::fromMap)

    override fun updateUserProfile(displayName: String?, profileImage: String?) {
        val uid = auth.uid
        if (uid != null) {
            val updateMap = mutableMapOf<String, Any>()
            if (displayName != null) {
                updateMap[User.FIELD_DISPLAY_NAME] = displayName
            }
            if (profileImage != null) {
                updateMap[User.FIELD_PROFILE_IMAGE] = profileImage
            }
            if (updateMap.keys.size > 0) {
                db.collection(COLLECTION_USER_PATH)
                    .document(uid)
                    .update(updateMap)
            }
        }
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

    private fun addUser(user: User) {
        db.collection(COLLECTION_USER_PATH)
            .document(user.uid!!)
            .set(user)
    }

    private fun createErrorData(message: String): LiveData<Resource<Void?>> {
        return MutableLiveData<Resource<Void?>>(Resource.error(Exception(message)))
    }
}