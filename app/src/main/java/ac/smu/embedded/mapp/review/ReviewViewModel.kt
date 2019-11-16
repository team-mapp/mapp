package ac.smu.embedded.mapp.review

import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.model.ReviewContent
import ac.smu.embedded.mapp.repository.ConfigLoaderRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.ReviewRepository
import ac.smu.embedded.mapp.repository.UserRepository
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

    val validRecommend: LiveData<Boolean> = useState(true)

    val validWaitingTime: LiveData<Boolean> = useState(true)

    val validEatenFood: LiveData<Boolean> = useState(true)

    val validReviewContent: LiveData<Boolean> = useState(true)

    val configObserver = configLoaderRepository.getDataUpdatedObserver()

    fun fetchConfig(): Map<String, Any> {
        val configs = mutableMapOf<String, Any>()
        configs[CONFIG_USE_BEST_PLACE] = configLoaderRepository.getBoolean(CONFIG_USE_BEST_PLACE)
        return configs
    }

    fun loadRestaurant(documentId: String) = viewModelScope.launch {
        setState(restaurant, restaurantsRepository.loadRestaurantAwait(documentId))
    }

    fun loadReview(documentId: String) = viewModelScope.launch {
        setState(review, reviewRepository.loadReviewAwait(documentId))
    }

    fun saveReview(restaurantId: String, reviewContent: ReviewContent) {
        if (validReview(reviewContent)) {
            val user = userRepository.getUser()
            if (user != null) {
                reviewRepository.addReview(restaurantId, user.uid!!, reviewContent)
            }
        }
    }

    fun updateReview(documentId: String, reviewContent: ReviewContent) {
        reviewRepository.updateReview(documentId, reviewContent)
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