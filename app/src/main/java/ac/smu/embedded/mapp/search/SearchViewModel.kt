package ac.smu.embedded.mapp.search

import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SearchViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository
) : StateViewModel() {
    val searchResults: LiveData<SearchResult> = useState()
    val searchAutoComplete: LiveData<SearchResult> = useState()
    val showProgress: LiveData<Boolean> = useState()

    fun search(query: String, autoComplete: Boolean = false) {
        viewModelScope.launch {
            setState(showProgress, !autoComplete)
            val queryCelebs = celebsRepository.loadCelebsByQuery(query)
            val queryPrograms = programsRepository.loadProgramsByQuery(query)
            val queryRestaurants = restaurantsRepository.loadRestaurantsByQuery(query)
            setState(showProgress, false)
            if (autoComplete) {
                setState(
                    searchAutoComplete,
                    SearchResult(queryCelebs, queryPrograms, queryRestaurants)
                )
            } else {
                setState(searchResults, SearchResult(queryCelebs, queryPrograms, queryRestaurants))
            }
        }
    }
}