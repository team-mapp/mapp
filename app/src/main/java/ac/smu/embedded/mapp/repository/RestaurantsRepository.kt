package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Restaurant.Companion.fromMap
import ac.smu.embedded.mapp.util.observe
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface RestaurantsRepository {

    suspend fun loadRestaurants(): List<Restaurant>?

    fun loadRestaurantsSync(): LiveData<List<Restaurant>?>

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

    override fun loadRestaurantsSync(): LiveData<List<Restaurant>?> =
        liveData {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }

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