package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.model.User

data class ReviewWithUser(val review: Review, val user: User?, val isSelf: Boolean = false)