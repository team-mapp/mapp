package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private val _printLogData = MutableLiveData<String>()
    val printLogData: LiveData<String> = _printLogData

    fun printLog(tag: String, message: String) {
        _printLogData.value = "($tag) $message"
    }

    fun loadCelebs(): LiveData<Resource<List<Celeb>?>> = celebsRepository.loadCelebs()

    fun loadCelebsSync(): LiveData<Resource<List<Celeb>?>> = celebsRepository.loadCelebsSync()

    fun loadCeleb(name: String): LiveData<Resource<Celeb?>> = celebsRepository.loadCeleb(name)

    fun loadPrograms(): LiveData<Resource<List<Program>?>> = programsRepository.loadPrograms()

    fun loadProgramsSync(): LiveData<Resource<List<Program>?>> =
        programsRepository.loadProgramsSync()

    fun loadProgram(name: String): LiveData<Resource<Program?>> =
        programsRepository.loadProgram(name)

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> =
        restaurantRepository.loadRestaurants()

    fun loadRestaurantsSync(): LiveData<Resource<List<Restaurant>?>> =
        restaurantRepository.loadRestaurantsSync()

    fun loadRestaurant(name: String): LiveData<Resource<Restaurant?>> =
        restaurantRepository.loadRestaurant(name)
}
