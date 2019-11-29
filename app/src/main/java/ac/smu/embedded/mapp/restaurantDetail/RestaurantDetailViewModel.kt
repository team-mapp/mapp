package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.Event
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.crashlytics.android.Crashlytics
import com.google.firebase.firestore.GeoPoint
import com.orhanobut.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RestaurantDetailViewModel(
    private val restaurantRepository: RestaurantsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) : StateViewModel() {

    val restaurant: LiveData<Restaurant?> = useState()
    val reviews: LiveData<List<ReviewWithUser>?> = useState()
    val visibleAddReview: LiveData<Boolean> = useState(false)
    val favorite: LiveData<Boolean> = useState()
    val launchPhone: LiveData<Event<String>> = useState()
    val launchAddress: LiveData<Event<Pair<String, GeoPoint>>> = useState()
    val copyText: LiveData<String> = useState()

    fun loadRestaurant(documentId: String) = viewModelScope.launch {
        setState(restaurant, restaurantRepository.loadRestaurant(documentId))
    }

    @ExperimentalCoroutinesApi
    fun loadReview(documentId: String) = viewModelScope.launch {
        val currentUser = userRepository.getUserWithoutProfile()
        reviewRepository.loadReviewsSync(documentId)
            .catch {
                Logger.t("loadReview($documentId)").e(it, it.toString())
                Crashlytics.logException(it)
            }
            .collect {
                var existUserReview = false
                val reviewWithUsers = it?.map { review ->
                    val user = userRepository.getUser(review.userId)
                    val isSelf = currentUser != null && currentUser == user?.uid
                    if (isSelf) existUserReview = true
                    ReviewWithUser(
                        review,
                        user,
                        currentUser != null && currentUser == user?.uid
                    )
                }
                setState(visibleAddReview, !existUserReview)
                setState(reviews, reviewWithUsers)
            }
    }


    fun loadFavorite(documentId: String) {
        viewModelScope.launch {
            val user = userRepository.getUserWithoutProfile()
            if (user != null) {
                val userFavorite = favoriteRepository.loadFavorite(user, documentId)
                setState(favorite, userFavorite != null)
            }
        }
    }

    fun toggleFavorite(documentId: String) = viewModelScope.launch {
        val favoriteState = favorite.value
        if (favoriteState != null) {
            val uid = userRepository.getUserWithoutProfile()
            if (uid != null) {
                if (favoriteState) {
                    setState(favorite, false)
                    favoriteRepository.removeFavorite(uid, documentId)
                } else {
                    setState(favorite, true)
                    favoriteRepository.addFavorite(uid, documentId)
                }
            }
        }
    }

    fun launchPhone() {
        val restaurant = this.restaurant.value
        if (restaurant != null) {
            setState(launchPhone, Event(restaurant.phone))
        }
    }

    fun launchAddress() {
        val restaurant = this.restaurant.value
        if (restaurant != null) {
            setState(launchAddress, Event(restaurant.name to restaurant.location))
        }
    }

    fun copyPhone() {
        val restaurant = this.restaurant.value
        if (restaurant != null) {
            setState(copyText, restaurant.phone)
        }
    }

    fun copyAddress() {
        val restaurant = this.restaurant.value
        if (restaurant != null) {
            setState(copyText, restaurant.address)
        }
    }
}
