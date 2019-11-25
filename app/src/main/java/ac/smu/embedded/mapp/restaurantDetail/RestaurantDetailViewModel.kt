package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantRepository: RestaurantsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : StateViewModel() {
    private val _review = MutableLiveData<List<Review>?>()
    val review: LiveData<List<Review>?> = _review

    val restaurant: LiveData<Restaurant?> = useState()

    fun loadUser(documentId: String) = viewModelScope.launch {
        userRepository.getUser()
    }

    fun loadRestaurant(documentId: String) = viewModelScope.launch {
        setState(restaurant, restaurantRepository.loadRestaurant(documentId))
    }

    fun loadReview(documentId: String) = viewModelScope.launch {
        _review.value = reviewRepository.loadReviews(documentId)
    }

    private val _favorite = MutableLiveData<Boolean>(false)
    val favorite: LiveData<Boolean> = _favorite

    // 좋아요 상태 확인
    fun loadFavorite(userId: String, documentId: String) = viewModelScope.launch {
        val favorites = favoriteRepository.loadFavorites(userId)
        if (favorites != null) {
            for (element in favorites) {
                _favorite.value = documentId == element.restaurantId
            }
        }
    }

    fun addFavorite(userId: String, documentId: String) =
        favoriteRepository.addFavorite(userId, documentId)

    fun removeFavorite(userId: String, documentId: String) =
        favoriteRepository.removeFavorite(userId, documentId)
}
