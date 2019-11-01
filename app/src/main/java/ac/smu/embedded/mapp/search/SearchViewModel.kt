package ac.smu.embedded.mapp.search

import ac.smu.embedded.mapp.model.Status
import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.util.combineLatest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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
        val queryCelebs = celebsRepository.loadCelebsByQuery(query)
        val queryPrograms = programsRepository.loadProgramsByQuery(query)
        val queryRestaurants = restaurantsRepository.loadRestaurantsByQuery(query)

        queryCelebs.combineLatest(queryPrograms).combineLatest(queryRestaurants).observeForever {
            val celebsResult = it.first.first
            val programsResult = it.first.second
            val restaurantsResult = it.second

            if (celebsResult.status == Status.SUCCESS
                && programsResult.status == Status.SUCCESS
                && restaurantsResult.status == Status.SUCCESS
            ) {
                if (autoComplete) {
                    _searchAutoComplete.value =
                        SearchResult(
                            celebsResult.data,
                            programsResult.data,
                            restaurantsResult.data
                        )
                } else {
                    _searchResults.value =
                        SearchResult(
                            celebsResult.data,
                            programsResult.data,
                            restaurantsResult.data
                        )
                }
            } else {
                if (autoComplete) {
                    _searchAutoComplete.value = null
                } else {
                    _searchResults.value = null
                }
            }
        }
    }
}