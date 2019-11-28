package ac.smu.embedded.mapp.detail

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DetailViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _celeb = MutableLiveData<Celeb?>()
    val celeb: LiveData<Celeb?> = _celeb

    private val _program = MutableLiveData<Program?>()
    val program: LiveData<Program?> = _program

    private val _restaurants = MutableLiveData<List<Restaurant>?>()
    val restaurants: LiveData<List<Restaurant>?> = _restaurants

    fun loadCeleb(documentId: String) = viewModelScope.launch {
        _celeb.value = celebsRepository.loadCeleb(documentId)
    }

    fun loadProgram(documentId: String) = viewModelScope.launch {
        _program.value = programsRepository.loadProgram(documentId)
    }

    fun loadRestaurants(documentIds: List<String>) =
        viewModelScope.launch {
            _restaurants.value = documentIds.mapNotNull {
                restaurantsRepository.loadRestaurant(it)
            }
        }

    fun addFavorite(documentId: String) =
        viewModelScope.launch {
            favoriteRepository.addFavorite(userRepository.getUser()?.uid!!, documentId)
        }

    fun removeFavorite(documentId: String) =
        viewModelScope.launch {
            favoriteRepository.removeFavorite(userRepository.getUser()?.uid!!, documentId)
        }
}

