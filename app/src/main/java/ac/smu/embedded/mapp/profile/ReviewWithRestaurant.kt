package ac.smu.embedded.mapp.profile

import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Review

data class ReviewWithRestaurant(val review: Review, val restaurant: Restaurant?)