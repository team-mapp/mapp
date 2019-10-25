package ac.smu.embedded.mapp.util

import ac.smu.embedded.mapp.BaseApplication
import ac.smu.embedded.mapp.main.MainViewModel
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
                application.programsRepository,
                application.restaurantsRepository,
                application.celebRelationsRepository,
                application.programRelationsRepository
            )
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}