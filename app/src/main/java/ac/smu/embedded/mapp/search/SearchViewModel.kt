package ac.smu.embedded.mapp.search

import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SearchViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository
) : ViewModel() {
    private val _searchResults = MutableLiveData<SearchResult?>()
    val searchResults = _searchResults

    private val _searchAutoComplete = MutableLiveData<SearchResult?>()
    val searchAutoComplete = _searchAutoComplete

    fun search(query: String, autoComplete: Boolean = false) {
        viewModelScope.launch {
            val queryCelebs = celebsRepository.loadCelebsByQuery(query)
            val queryPrograms = programsRepository.loadProgramsByQuery(query)
            val queryRestaurants = restaurantsRepository.loadRestaurantsByQuery(query)

            if (autoComplete) {
                _searchAutoComplete.value =
                    SearchResult(queryCelebs, queryPrograms, queryRestaurants)
            } else {
                _searchResults.value = SearchResult(queryCelebs, queryPrograms, queryRestaurants)
            }
        }
    }
}