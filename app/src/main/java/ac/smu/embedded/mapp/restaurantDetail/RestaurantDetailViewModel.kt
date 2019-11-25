package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.model.Favorite
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantRepository: RestaurantsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository

    ): ViewModel() {

    fun loadUser(documentId: String) = userRepository.getUser()

    fun loadRestaurant(documentId: String) = restaurantRepository.loadRestaurant(documentId)

    private val _review =MutableLiveData<List<Review>?>()
    val review:LiveData<List<Review>?> = _review

    fun loadReview(documentId: String) = viewModelScope.launch {
        _review.value = reviewRepository.loadReviewsAwait(documentId)

    }

    private val _favorite = MutableLiveData<Boolean>(false)
    val favorite: LiveData<Boolean> = _favorite

    // 좋아요 상태 확인
    fun loadFavorite(userId: String, documentId: String) = viewModelScope.launch {
        val favorites = favoriteRepository.loadFavoritesAwait(userId)
        if (favorites != null) {
            for (i in 0 until favorites.size) {
                if (documentId == favorites[i].restaurantId){
                    _favorite.value = true

                }else{
                    _favorite.value =false
                }

            }
        }
    }

    fun addFavorite(userId: String, documentId: String) =
        favoriteRepository.addFavorite(userId, documentId)

    fun removeFavorite(userId: String, documentId: String) =
        favoriteRepository.removeFavorite(userId, documentId)
}
