package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.Notification
import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.local.NotificationDao
import ac.smu.embedded.mapp.util.NotificationConstants
import ac.smu.embedded.mapp.util.StateViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val notificationDao: NotificationDao
) : StateViewModel() {

    val notifications = useState<List<Notification>>()

    fun loadNotifications() =
        CoroutineScope(Dispatchers.IO).launch {
            notificationDao.loadAll().collect { list ->
                val mappedNotifications = list.map {
                    if (it.type == NotificationConstants.TYPE_REVIEW_CREATED) {
                        val restaurant = restaurantsRepository.loadRestaurantAwait(it.content)
                        if (restaurant != null) {
                            it.data = restaurant
                        }
                    }
                    return@map it
                }
                viewModelScope.launch {
                    setState(notifications, mappedNotifications)
                }
            }
        }

    fun loadCelebs() = celebsRepository.loadCelebsSync(viewModelScope)

    fun loadPrograms() = programsRepository.loadProgramsSync(viewModelScope)

}
