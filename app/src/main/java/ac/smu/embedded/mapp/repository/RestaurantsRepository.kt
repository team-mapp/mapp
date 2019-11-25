package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Restaurant.Companion.fromMap
import ac.smu.embedded.mapp.util.asFlow
import ac.smu.embedded.mapp.util.toObject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface RestaurantsRepository {

    suspend fun loadRestaurants(): List<Restaurant>?

    fun loadRestaurantsSync(): Flow<List<Restaurant>?>

    suspend fun loadRestaurantsByQuery(query: String): List<Restaurant>?

    suspend fun loadRestaurant(documentId: String): Restaurant?

    suspend fun loadRestaurantByName(name: String): Restaurant?

}

class RestaurantsRepositoryImpl(private val db: FirebaseFirestore) : RestaurantsRepository {

    companion object {
        private const val COLLECTION_PATH = "restaurants"
    }

    override suspend fun loadRestaurants(): List<Restaurant>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    @ExperimentalCoroutinesApi
    override fun loadRestaurantsSync(): Flow<List<Restaurant>?> =
        db.collection(COLLECTION_PATH)
            .asFlow()
            .map { it?.toObject(::fromMap) }

    override suspend fun loadRestaurantsByQuery(query: String): List<Restaurant>? =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadRestaurant(documentId: String): Restaurant? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadRestaurantByName(name: String): Restaurant? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)
}