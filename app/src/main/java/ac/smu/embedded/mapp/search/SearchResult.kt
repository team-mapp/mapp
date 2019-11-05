package ac.smu.embedded.mapp.search

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Restaurant

data class SearchResult(
    val celebsResult: List<Celeb>?,
    val programsResult: List<Program>?,
    val restaurantResult: List<Restaurant>?
)