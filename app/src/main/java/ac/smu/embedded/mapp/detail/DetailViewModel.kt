package ac.smu.embedded.mapp.detail

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.model.Status
import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.util.LiveDataUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class DetailViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository
) : ViewModel() {
    private val _restaurants = MutableLiveData<List<Restaurant>?>()
    val restaurants: LiveData<List<Restaurant>?> = _restaurants

    private lateinit var restaurantsObserver: Observer<List<Resource<Restaurant?>>>

    fun loadCeleb(documentId: String) = celebsRepository.loadCeleb(documentId)

    fun loadProgram(documentId: String) = programsRepository.loadProgram(documentId)

    fun loadRestaurants(documentIds: List<String>) {
        val restaurantDataList = documentIds.map {
            restaurantsRepository.loadRestaurant(it)
        }
        val zippedData = LiveDataUtil.zip(restaurantDataList)
        restaurantsObserver = Observer { list ->
            val restaurantList = list.filter {
                it.status == Status.SUCCESS && it.data != null
            }.map { it.data!! }

            if (restaurantList.size == documentIds.size) {
                _restaurants.value = restaurantList
                zippedData.removeObserver(restaurantsObserver)
            }
        }
        zippedData.observeForever(restaurantsObserver)
    }
}