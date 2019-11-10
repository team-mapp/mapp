package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.UserFavorite
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.await
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

interface UserFavoriteRepository {

    fun loadUserFavorites(userId: String): LiveData<Resource<List<UserFavorite>?>>

    fun loadUserFavoritesSync(scope: CoroutineScope, userId: String): LiveData<List<UserFavorite>?>

    suspend fun loadUserFavoritesAwait(userId: String): List<UserFavorite>?

    fun addUserFavorite(userId: String, restaurantId: String)

    fun removeUserFavorite(userId: String, restaurantId: String)

}

class UserFavoriteRepositoryImpl(private val db: FirebaseFirestore) : UserFavoriteRepository {

    companion object {
        private const val COLLECTION_PATH = "user_favorites"
    }

    override fun loadUserFavorites(userId: String): LiveData<Resource<List<UserFavorite>?>> =
        db.collection(COLLECTION_PATH).whereEqualTo(
            UserFavorite.FIELD_USER_ID,
            userId
        ).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    UserFavorite.fromMap(it.id, it.data!!)
                }
            }
        }

    override fun loadUserFavoritesSync(
        scope: CoroutineScope,
        userId: String
    ): LiveData<List<UserFavorite>?> {
        return liveData(scope.coroutineContext) {
            val snapshot = db.collection(COLLECTION_PATH).await()
            val list =
                snapshot.documents
                    .filter { it.data != null }
                    .map { UserFavorite.fromMap(it.id, it.data!!) }
            if (list.isNotEmpty()) emit(list.toList())
            else emit(null)
        }
    }

    override suspend fun loadUserFavoritesAwait(userId: String): List<UserFavorite>? {
        val snapshot = db.collection(COLLECTION_PATH).get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { UserFavorite.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
    }

    override fun addUserFavorite(userId: String, restaurantId: String) {
        db.collection(COLLECTION_PATH).add(UserFavorite("", userId, restaurantId))
    }

    override fun removeUserFavorite(userId: String, restaurantId: String) {
        db.collection(COLLECTION_PATH)
            .whereEqualTo(UserFavorite.FIELD_USER_ID, userId)
            .whereEqualTo(UserFavorite.FIELD_RESTAURANT_ID, restaurantId)
            .get()
            .addOnCompleteListener {
                val document = it.result?.firstOrNull()
                document?.reference?.delete()
            }
    }
}