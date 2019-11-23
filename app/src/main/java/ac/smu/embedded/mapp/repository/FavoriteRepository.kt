package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Favorite
import ac.smu.embedded.mapp.model.Favorite.Companion.fromMap
import ac.smu.embedded.mapp.util.asFlow
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await

interface FavoriteRepository {

    suspend fun loadFavorites(userId: String): List<Favorite>?

    fun loadFavoritesSync(userId: String): LiveData<List<Favorite>?>

    suspend fun loadFavorite(userId: String, restaurantId: String): Favorite?

    fun addFavorite(userId: String, restaurantId: String)

    fun removeFavorite(userId: String, restaurantId: String)

}

class FavoriteRepositoryImpl(private val db: FirebaseFirestore) : FavoriteRepository {

    companion object {
        private const val COLLECTION_PATH = "favorites"
    }

    override suspend fun loadFavorites(userId: String): List<Favorite>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Favorite.FIELD_USER_ID, userId)
            .get().await()
            .toObject(::fromMap)

    @ExperimentalCoroutinesApi
    override fun loadFavoritesSync(userId: String): LiveData<List<Favorite>?> =
        liveData {
            db.collection(COLLECTION_PATH)
                .whereEqualTo(Favorite.FIELD_USER_ID, userId)
                .asFlow()
                .collect {
                    emit(it?.toObject(::fromMap))
                }
        }

    override suspend fun loadFavorite(userId: String, restaurantId: String): Favorite? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Favorite.FIELD_USER_ID, userId)
            .whereEqualTo(Favorite.FIELD_RESTAURANT_ID, restaurantId)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)

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