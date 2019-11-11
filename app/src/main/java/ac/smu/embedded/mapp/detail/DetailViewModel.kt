package ac.smu.embedded.mapp.detail

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.FavoriteRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DetailViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val favoriteRepository: FavoriteRepository
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

    fun loadRestaurants(userId: String, documentIds: List<String>) =
        viewModelScope.launch {
            val userFavorites = favoriteRepository.loadFavoritesAwait(userId)
            val restaurants = documentIds.map {
                restaurantsRepository.loadRestaurantAwait(it)
            }
            val userFavoriteIds = userFavorites?.map { it.restaurantId }
            _restaurants.value = restaurants.filterNotNull().map {
                it.isFavorite = userFavoriteIds?.contains(it.documentId) ?: false
                return@map it
            }
        }

    fun addFavorite(userId: String, documentId: String) =
        favoriteRepository.addFavorite(userId, documentId)

    fun removeFavorite(userId: String, documentId: String) =
        favoriteRepository.removeFavorite(userId, documentId)
}