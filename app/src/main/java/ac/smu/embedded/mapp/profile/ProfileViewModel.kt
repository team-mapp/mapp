package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.model.Favorite
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.StorageRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.StateViewModel
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val storageRepository: StorageRepository<UploadTask>,
    private val favoriteRepository: FavoriteRepository,
    private val restaurantsRepository: RestaurantsRepository
) : StateViewModel() {

    val favorites: LiveData<List<Favorite>?> = useState()

    val favoriteRestaurants: LiveData<List<Restaurant>?> = useState()

    fun loadFavorites() = viewModelScope.launch {
        val user = userRepository.getUser()
        if (user != null) {
            favoriteRepository.loadFavoritesSync(user.uid!!).asFlow().collect {
                setState(favorites, it)
            }
        }
    }

    fun loadFavoriteRestaurants(favorites: List<Favorite>) =
        viewModelScope.launch {
            val documentIds = favorites.map { it.restaurantId }
            val restaurantList = documentIds.map {
                restaurantsRepository.loadRestaurant(it)!!
            }
            setState(favoriteRestaurants, restaurantList)
        }

    fun uploadImage(uri: Uri, userId: String) =
        storageRepository.put("$userId.jpg", uri)
}