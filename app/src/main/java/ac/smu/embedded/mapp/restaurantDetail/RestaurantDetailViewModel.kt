package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.Event
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    val reviews: LiveData<List<ReviewWithUser>?> = useState()
    val user: LiveData<User?> = useState()
    val restaurant: LiveData<Restaurant?> = useState()
    val launchPhone: LiveData<Event<String>> = useState()
    val launchAddress: LiveData<Event<Pair<String, GeoPoint>>> = useState()
    val copyText: LiveData<String> = useState()

    fun loadRestaurant(documentId: String) = viewModelScope.launch {
        setState(restaurant, restaurantRepository.loadRestaurant(documentId))
    }

    @ExperimentalCoroutinesApi
    fun loadReview(documentId: String) = viewModelScope.launch {
        val currentUser = userRepository.getUser()
        reviewRepository.loadReviewsSync(documentId)
            .catch {
                Logger.t("loadReview($documentId)").e(it, it.toString())
                Crashlytics.logException(it)
            }
            .collect {
                val reviewWithUsers = it?.map { review ->
                    val user = userRepository.getUser(review.userId)
                    ReviewWithUser(
                        review,
                        user,
                        currentUser != null && currentUser.uid == user?.uid
                    )
                }
                setState(reviews, reviewWithUsers)
            }
    }

    private val _favorite = MutableLiveData<Boolean>(false)
    val favorite: LiveData<Boolean> = _favorite

    // 좋아요 상태 확인
    fun loadFavorite(documentId: String) = viewModelScope.launch {
        val user = userRepository.getUser()
        if (user != null) {
            val favorites = favoriteRepository.loadFavorites(user.uid!!)
            if (favorites != null) {
                for (element in favorites) {
                    _favorite.value = documentId == element.restaurantId
                }
            }
        }
    }

    fun addFavorite(documentId: String) = viewModelScope.launch {
        val user = userRepository.getUser()
        if (user != null) {
            favoriteRepository.addFavorite(user.uid!!, documentId)
        }
    }


    fun removeFavorite(documentId: String) = viewModelScope.launch {
        val user = userRepository.getUser()
        if (user != null) {
            favoriteRepository.removeFavorite(user.uid!!, documentId)
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
