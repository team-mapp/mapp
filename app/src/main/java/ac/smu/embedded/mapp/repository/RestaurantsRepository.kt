package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.observe
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

    override fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    override fun loadRestaurantsSync(scope: CoroutineScope): LiveData<List<Restaurant>?> {
        return liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                val list =
                    snapshot.documents
                        .filter { it.data != null }
                        .map { Restaurant.fromMap(it.id, it.data!!) }
                if (list.isNotEmpty()) emit(list.toList())
                else emit(null)
            }
        }
    }

    override fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    override suspend fun loadRestaurantsOnceAwait(): List<Restaurant>? {
        val snapshot = db.collection(COLLECTION_PATH).get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { Restaurant.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
    }

    override fun loadRestaurantsByQuery(query: String): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH)
            .whereArrayContains(
                Restaurant.FIELD_INDICES,
                query
            ).get()
            .asLiveData().map { resource ->
                resource.transform { snapshot ->
                    snapshot?.documents?.map {
                        Restaurant.fromMap(it.id, it.data!!)
                    }
                }
            }
    }

    override suspend fun loadRestaurantsByQueryAwait(query: String): List<Restaurant>? {
        val snapshot = db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { Restaurant.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
    }

    override fun loadRestaurant(documentId: String): LiveData<Resource<Restaurant?>> {
        return db.collection(COLLECTION_PATH).document(documentId).get().asLiveData()
            .map { resource ->
                resource.transform {
                    if (it != null) {
                        Restaurant.fromMap(it.id, it.data!!)
                    } else {
                        null
                    }
                }
            }
    }

    override suspend fun loadRestaurantAwait(documentId: String): Restaurant? {
        val snapshot = db.collection(COLLECTION_PATH).document(documentId).get().await()
        return if (snapshot.data != null) {
            Restaurant.fromMap(snapshot.id, snapshot.data!!)
        } else {
            null
        }
    }

    override fun loadRestaurantByName(name: String): LiveData<Resource<Restaurant?>> {
        return db.collection(COLLECTION_PATH).whereEqualTo(
            Restaurant.FIELD_NAME,
            name
        ).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                val document = snapshot?.firstOrNull()
                if (document != null) {
                    Restaurant.fromMap(document.id, document.data)
                } else {
                    null
                }
            }
        }
    }

    override suspend fun loadRestaurantByNameAwait(name: String): Restaurant? {
        val snapshot = db.collection(COLLECTION_PATH).whereEqualTo(
            Celeb.FIELD_NAME,
            name
        ).get().await()

        val document = snapshot.firstOrNull()
        return if (document != null) {
            Restaurant.fromMap(document.id, document.data)
        } else {
            return null
        }
    }
}