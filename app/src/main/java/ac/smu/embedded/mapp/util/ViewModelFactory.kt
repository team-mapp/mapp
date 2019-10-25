package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.main.MainViewModel
import ac.smu.embedded.mapp.repository.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val celebRelationsRepository: CelebRelationsRepository,
    private val programRelationsRepository: ProgramRelationsRepository
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(MainViewModel::class.java) -> MainViewModel(
                celebsRepository,
                programsRepository,
                restaurantsRepository,
                celebRelationsRepository,
                programRelationsRepository
            )
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}