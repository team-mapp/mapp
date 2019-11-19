package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.model.Favorite
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.StorageRepository
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.launch

class ProfileViewModel (
    private val storageRepository: StorageRepository<UploadTask>,
    private val favoriteRepository: FavoriteRepository,
    private val restaurantsRepository: RestaurantsRepository
) : ViewModel() {

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> = _restaurants

    fun uploadImage(uri: Uri, userId: String) =
        storageRepository.put(userId + ".jpg", uri)

    fun loadRestaurants(userId: String, documentIds: List<String>) =
        viewModelScope.launch {
            val userFavorites = favoriteRepository.loadFavoritesAwait(userId)
            val restaurants = documentIds.map {
                restaurantsRepository.loadRestaurantAwait(it)
            }
            val userFavoriteIds = userFavorites?.map { it.restaurantId }
            _restaurants.value = restaurants.filterNotNull().map {
                it.isFavorite = userFavoriteIds?.contains(it.documentId) ?: false
                return@map it
            }
        }
}