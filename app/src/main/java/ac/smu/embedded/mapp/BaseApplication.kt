package ac.smu.embedded.mapp

import ac.smu.embedded.mapp.detail.DetailViewModel
import ac.smu.embedded.mapp.main.MainViewModel
import ac.smu.embedded.mapp.repository.*
import ac.smu.embedded.mapp.search.SearchViewModel
import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApplication : Application() {
    private val firestore: FirebaseFirestore
        get() = Firebase.firestore

    private val repositoryModule = module {

        single<CelebsRepository> { CelebsRepositoryImpl(firestore) }

        single<ProgramsRepository> { ProgramsRepositoryImpl(firestore) }

        single<RestaurantsRepository> { RestaurantsRepositoryImpl(firestore) }

        single<CelebRelationsRepository> { CelebRelationsRepositoryImpl(firestore) }

        single<ProgramRelationsRepository> { ProgramRelationsRepositoryImpl(firestore) }

    }

    private val viewModelModule = module {
        viewModel { MainViewModel(get(), get()) }

        viewModel { DetailViewModel(get(), get(), get(), get(), get()) }

        viewModel { SearchViewModel(get(), get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}