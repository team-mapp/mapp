package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Restaurant.Companion.fromMap
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.observe
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

interface RestaurantsRepository {

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>>

    fun loadRestaurantsSync(scope: CoroutineScope): LiveData<List<Restaurant>?>

    fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>>

    suspend fun loadRestaurantsOnceAwait(): List<Restaurant>?

    fun loadRestaurantsByQuery(query: String): LiveData<Resource<List<Restaurant>?>>

    suspend fun loadRestaurantsByQueryAwait(query: String): List<Restaurant>?

    fun loadRestaurant(documentId: String): LiveData<Resource<Restaurant?>>

    suspend fun loadRestaurantAwait(documentId: String): Restaurant?

    fun loadRestaurantByName(name: String): LiveData<Resource<Restaurant?>>

    suspend fun loadRestaurantByNameAwait(name: String): Restaurant?

}

class RestaurantsRepositoryImpl(private val db: FirebaseFirestore) : RestaurantsRepository {

    companion object {
        private const val COLLECTION_PATH = "restaurants"
    }

    override fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> =
        db.collection(COLLECTION_PATH)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override fun loadRestaurantsSync(scope: CoroutineScope): LiveData<List<Restaurant>?> =
        liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }

    override fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>> =
        db.collection(COLLECTION_PATH)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadRestaurantsOnceAwait(): List<Restaurant>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    override fun loadRestaurantsByQuery(query: String): LiveData<Resource<List<Restaurant>?>> =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadRestaurantsByQueryAwait(query: String): List<Restaurant>? =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
            .toObject(::fromMap)

    override fun loadRestaurant(documentId: String): LiveData<Resource<Restaurant?>> =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadRestaurantAwait(documentId: String): Restaurant? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await()
            .toObject(::fromMap)

    override fun loadRestaurantByName(name: String): LiveData<Resource<Restaurant?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.firstOrNull()?.toObject(::fromMap)
                }
            }

    override suspend fun loadRestaurantByNameAwait(name: String): Restaurant? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)
}