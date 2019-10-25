package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.transform
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantRepository(private val db: FirebaseFirestore) {

    companion object {
        private const val COLLECTION_PATH = "restaurants"
    }

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.data!!)
                }
            }
        }
    }

    fun loadRestaurantsSync(): LiveData<Resource<List<Restaurant>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Restaurant.fromMap(it.data!!)
                }
            }
        }
    }

    fun loadRestaurant(name: String): LiveData<Resource<Restaurant?>> {
        return db.collection(COLLECTION_PATH).whereEqualTo(
            Restaurant.FIELD_NAME,
            name
        ).asLiveData().map { resource ->
            resource.transform { snapshot ->
                val document = snapshot?.firstOrNull()
                if (document != null) {
                    Restaurant.fromMap(document.data)
                } else {
                    null
                }
            }
        }
    }
}