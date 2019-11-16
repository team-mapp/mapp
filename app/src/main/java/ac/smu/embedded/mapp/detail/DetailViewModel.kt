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
        _celeb.value = celebsRepository.loadCelebAwait(documentId)
    }

    fun loadProgram(documentId: String) = viewModelScope.launch {
        _program.value = programsRepository.loadProgramAwait(documentId)
    }

    fun loadRestaurants(documentIds: List<String>) =
        viewModelScope.launch {
            val userFavorites =
                favoriteRepository.loadFavoritesAwait(userRepository.getUser()?.uid!!)
            val restaurants = documentIds.map {
                restaurantsRepository.loadRestaurantAwait(it)
            }
            val userFavoriteIds = userFavorites?.map { it.restaurantId }
            _restaurants.value = restaurants.filterNotNull().map {
                it.isFavorite = userFavoriteIds?.contains(it.documentId) ?: false
                return@map it
            }
        }

    fun addFavorite(documentId: String) =
        favoriteRepository.addFavorite(userRepository.getUser()?.uid!!, documentId)

    fun removeFavorite(documentId: String) =
        favoriteRepository.removeFavorite(userRepository.getUser()?.uid!!, documentId)
}