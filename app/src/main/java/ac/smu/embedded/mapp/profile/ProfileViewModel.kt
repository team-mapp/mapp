package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.model.Favorite
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.crashlytics.android.Crashlytics
import com.orhanobut.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val favoriteRepository: FavoriteRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val reviewRepository: ReviewRepository
) : StateViewModel() {

    val favorites: LiveData<List<Favorite>?> = useState()

    val reviews: LiveData<List<ReviewWithRestaurant>?> = useState()

    val favoriteRestaurants: LiveData<List<Restaurant>?> = useState()

    fun loadFavorites() = viewModelScope.launch {
        val user = userRepository.getUserWithoutProfile()
        if (user != null) {
            favoriteRepository.loadFavoritesSync(user).collect {
                setState(favorites, it)
            }
        }
    }

    fun loadFavoriteRestaurants(favorites: List<Favorite>) = viewModelScope.launch {
        val documentIds = favorites.map { it.restaurantId }
        val restaurantList = documentIds.map {
            restaurantsRepository.loadRestaurant(it)!!
        }
        setState(favoriteRestaurants, restaurantList)
    }

    @ExperimentalCoroutinesApi
    fun loadUserReviews() = viewModelScope.launch {
        val user = userRepository.getUserWithoutProfile()
        if (user != null) {
            reviewRepository.loadUserReviewsSync(user)
                .catch {
                    Logger.t("loadUserReviews").e(it, it.toString())
                    Crashlytics.logException(it)
                }
                .collect { list ->
                    val listWithRestaurant = list?.map {
                        ReviewWithRestaurant(
                            it,
                            restaurantsRepository.loadRestaurant(it.restaurantId)
                        )
                    }
                    setState(reviews, listWithRestaurant)
                }
        }
    }

    fun removeReview(documentId: String) = reviewRepository.removeReview(documentId)
}