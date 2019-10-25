package ac.smu.embedded.mapp

import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore

class BaseApplication : Application() {
    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    val celebsRepository: CelebsRepository
        get() = CelebsRepository(firestore)

    val programsRepository: ProgramsRepository
        get() = ProgramsRepository(firestore)

    val restaurantsRepository: RestaurantsRepository
        get() = RestaurantsRepository(firestore)

    override fun onCreate() {
        super.onCreate()
    }
}