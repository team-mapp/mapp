package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Favorite
import ac.smu.embedded.mapp.model.Favorite.Companion.fromMap
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.observe
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

interface FavoriteRepository {

    fun loadFavorites(userId: String): LiveData<Resource<List<Favorite>?>>

    fun loadFavoritesSync(scope: CoroutineScope, userId: String): LiveData<List<Favorite>?>

    suspend fun loadFavoritesAwait(userId: String): List<Favorite>?

    fun addFavorite(userId: String, restaurantId: String)

    fun removeFavorite(userId: String, restaurantId: String)

}

class FavoriteRepositoryImpl(private val db: FirebaseFirestore) : FavoriteRepository {

    companion object {
        private const val COLLECTION_PATH = "favorites"
    }

    override fun loadFavorites(userId: String): LiveData<Resource<List<Favorite>?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Favorite.FIELD_USER_ID, userId)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override fun loadFavoritesSync(
        scope: CoroutineScope,
        userId: String
    ): LiveData<List<Favorite>?> =
        liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }

    override suspend fun loadFavoritesAwait(userId: String): List<Favorite>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    override fun addFavorite(userId: String, restaurantId: String) {
        db.collection(COLLECTION_PATH)
            .add(Favorite("", userId, restaurantId))
    }

    override fun removeFavorite(userId: String, restaurantId: String) {
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Favorite.FIELD_USER_ID, userId)
            .whereEqualTo(Favorite.FIELD_RESTAURANT_ID, restaurantId)
            .get()
            .addOnCompleteListener {
                val document = it.result?.firstOrNull()
                document?.reference?.delete()
            }
    }
}