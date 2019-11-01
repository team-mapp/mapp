package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantsRepository(private val db: FirebaseFirestore) {

    companion object {
        private const val COLLECTION_PATH = "restaurants"
    }

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    fun loadRestaurantsByQuery(query: String): LiveData<Resource<List<Restaurant>?>> {
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

    fun loadRestaurant(documentId: String): LiveData<Resource<Restaurant?>> {
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

    fun loadRestaurantByName(name: String): LiveData<Resource<Restaurant?>> {
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