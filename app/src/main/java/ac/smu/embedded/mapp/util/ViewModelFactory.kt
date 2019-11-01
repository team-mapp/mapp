package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.BaseApplication
import ac.smu.embedded.mapp.detail.DetailViewModel
import ac.smu.embedded.mapp.main.MainViewModel
import ac.smu.embedded.mapp.search.SearchViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val application: BaseApplication
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                application.celebsRepository,
                application.programsRepository
            )
            isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel(
                application.celebsRepository,
                application.celebRelationsRepository,
                application.programsRepository,
                application.programRelationsRepository,
                application.restaurantsRepository
            )
            isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(
                application.celebsRepository,
                application.programsRepository,
                application.restaurantsRepository
            )
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}