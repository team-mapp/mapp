package ac.smu.embedded.mapp.review

import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.model.ReviewContent
import ac.smu.embedded.mapp.model.User
import ac.smu.embedded.mapp.repository.ConfigLoaderRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.util.CONFIG_USE_ABOUT_FOOD
import ac.smu.embedded.mapp.util.CONFIG_USE_ABOUT_PLACE
import ac.smu.embedded.mapp.util.CONFIG_USE_BEST_PLACE
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val restaurantsRepository: RestaurantsRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
    private val configLoaderRepository: ConfigLoaderRepository
) : StateViewModel() {

    val restaurant: LiveData<Restaurant?> = useState()

    val review: LiveData<Review?> = useState()

    val user: LiveData<User?> = useState()

    val validRecommend: LiveData<Boolean> = useState(true)

    val validWaitingTime: LiveData<Boolean> = useState(true)

    val validEatenFood: LiveData<Boolean> = useState(true)

    val validReviewContent: LiveData<Boolean> = useState(true)

    val contentSaved: LiveData<Boolean> = useState(false)

    val configUpdated = configLoaderRepository.getDataUpdatedObserver()

    val configs: LiveData<Map<String, Any>> = useState()

    fun fetchConfig() {
        val updateConfigs = mapOf(
            CONFIG_USE_BEST_PLACE to configLoaderRepository.getBoolean(CONFIG_USE_BEST_PLACE),
            CONFIG_USE_ABOUT_PLACE to configLoaderRepository.getBoolean(CONFIG_USE_ABOUT_PLACE),
            CONFIG_USE_ABOUT_FOOD to configLoaderRepository.getBoolean(CONFIG_USE_ABOUT_FOOD)
        )
        setState(configs, updateConfigs)
    }

    fun loadRestaurant(documentId: String) = viewModelScope.launch {
        setState(restaurant, restaurantsRepository.loadRestaurant(documentId))
    }

    fun loadReview(documentId: String) = viewModelScope.launch {
        setState(review, reviewRepository.loadReview(documentId))
    }

    fun loadUser(uid: String) = viewModelScope.launch {
        setState(user, userRepository.getUser(uid))
    }

    fun saveReview(
        restaurantId: String,
        documentId: String? = null,
        reviewContent: ReviewContent
    ) = viewModelScope.launch {
        if (validReview(reviewContent)) {
            val user = userRepository.getUserWithoutProfile()
            if (user != null) {
                if (documentId == null) {
                    reviewRepository.addReview(restaurantId, user, reviewContent)
                } else {
                    reviewRepository.updateReview(documentId, reviewContent)
                }
                setState(contentSaved, true)
            }
        }
    }

    private fun validReview(reviewContent: ReviewContent): Boolean {
        var valid = true

        if (reviewContent.recommendPoint != null) {
            setState(validRecommend, true)
        } else {
            setState(validRecommend, false)
            valid = false
        }

        if (reviewContent.waitingTime != null) {
            setState(validWaitingTime, true)
        } else {
            setState(validWaitingTime, false)
            valid = false
        }

        if (reviewContent.eatenFood != null && reviewContent.eatenFood.isNotEmpty()) {
            setState(validEatenFood, true)
        } else {
            setState(validEatenFood, false)
            valid = false
        }

        if (reviewContent.detailAnswer != null && reviewContent.detailAnswer.isNotEmpty()) {
            setState(validReviewContent, true)
        } else {
            setState(validReviewContent, false)
            valid = false
        }
        return valid
    }
}