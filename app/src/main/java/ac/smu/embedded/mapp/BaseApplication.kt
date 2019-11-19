package ac.smu.embedded.mapp

import ac.smu.embedded.mapp.Intro.IntroViewModel
import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.detail.DetailViewModel
import ac.smu.embedded.mapp.main.MainViewModel
import ac.smu.embedded.mapp.repository.*
import ac.smu.embedded.mapp.search.SearchViewModel
import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class BaseApplication : Application() {
    private val firestore: FirebaseFirestore
        get() = Firebase.firestore

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val firebaseStorage: FirebaseStorage
        get() = Firebase.storage

    private val repositoryModule = module {

        single<CelebsRepository> { CelebsRepositoryImpl(firestore) }

        single<ProgramsRepository> { ProgramsRepositoryImpl(firestore) }

        single<RestaurantsRepository> { RestaurantsRepositoryImpl(firestore) }

        single<FavoriteRepository> { FavoriteRepositoryImpl(firestore) }

        single<ReviewRepository> { ReviewRepositoryImpl(firestore) }

        single<UserRepository> { UserRepositoryImpl(firebaseAuth) }

        single<StorageRepository<*>> { StorageRepositoryImpl(firebaseStorage) }

    }

    private val viewModelModule = module {
        viewModel { MainViewModel(get(), get()) }

        viewModel { DetailViewModel(get(), get(), get(), get()) }

        viewModel { SearchViewModel(get(), get(), get()) }

        viewModel { UserViewModel(get()) }

        viewModel { IntroViewModel(get(), get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(repositoryModule, viewModelModule))
        }

        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(AndroidLogAdapter())
        }
    }
}