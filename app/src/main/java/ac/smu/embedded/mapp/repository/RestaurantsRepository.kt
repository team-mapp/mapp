package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

interface RestaurantsRepository {

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>>

    fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>>

    fun loadRestaurantsByQuery(query: String): LiveData<Resource<List<Restaurant>?>>

    fun loadRestaurant(documentId: String): LiveData<Resource<Restaurant?>>

    fun loadRestaurantByName(name: String): LiveData<Resource<Restaurant?>>

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

    override fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.id, it.data!!)
                }
            }
        }
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
}