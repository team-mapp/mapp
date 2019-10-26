package ac.smu.embedded.mapp

import ac.smu.embedded.mapp.repository.*
import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class BaseApplication : Application() {
    private val firestore: FirebaseFirestore
        get() = Firebase.firestore

    val storage: FirebaseStorage
        get() = Firebase.storage
 
    val celebsRepository: CelebsRepository
        get() = CelebsRepository(firestore)

    val programsRepository: ProgramsRepository
        get() = ProgramsRepository(firestore)

    val restaurantsRepository: RestaurantsRepository
        get() = RestaurantsRepository(firestore)

    val celebRelationsRepository: CelebRelationsRepository
        get() = CelebRelationsRepository(firestore)

    val programRelationsRepository: ProgramRelationsRepository
        get() = ProgramRelationsRepository(firestore)

    override fun onCreate() {
        super.onCreate()
    }
}