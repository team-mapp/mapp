package ac.smu.embedded.mapp.restaurantDetail

import ac.smu.embedded.mapp.repository.RestaurantsRepository
import androidx.lifecycle.ViewModel

class RestaurantDetailViewModel(private val restaurantRepository: RestaurantsRepository) : ViewModel() {

    fun loadRestaurant(documentId: String) = restaurantRepository.loadRestaurant(documentId)
}