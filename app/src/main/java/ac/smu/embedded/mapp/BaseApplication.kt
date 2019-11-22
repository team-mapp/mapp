package ac.smu.embedded.mapp

import ac.smu.embedded.mapp.common.UserViewModel
import ac.smu.embedded.mapp.detail.DetailViewModel
import ac.smu.embedded.mapp.intro.IntroViewModel
import ac.smu.embedded.mapp.main.MainViewModel
import ac.smu.embedded.mapp.profile.FavoriteViewModel
import ac.smu.embedded.mapp.profile.ProfileViewModel
import ac.smu.embedded.mapp.repository.*
import ac.smu.embedded.mapp.repository.local.AppDatabase
import ac.smu.embedded.mapp.review.ReviewViewModel
import ac.smu.embedded.mapp.search.SearchViewModel
import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val CONFIG_REMOTE = "remote"
private const val CONFIG_LOCAL = "local"

class BaseApplication : Application() {
    private val firestore: FirebaseFirestore
        get() = Firebase.firestore.apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }

    private val firebaseAuth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val firebaseStorage: FirebaseStorage
        get() = Firebase.storage

    private val firebaseConfig: FirebaseRemoteConfig
        get() {
            val remoteConfig = Firebase.remoteConfig
            if (BuildConfig.DEBUG) {
                val config = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
                remoteConfig.setConfigSettingsAsync(config)
            }
            return remoteConfig
        }

    private val db: AppDatabase
        get() = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mapp-db"
        ).build()


    private val appPreference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(
            applicationContext
        )

    private val daoModule = module {

        single { db.notificationDao() }

    }

    private val repositoryModule = module {

        single<CelebsRepository> { CelebsRepositoryImpl(firestore) }

        single<ProgramsRepository> { ProgramsRepositoryImpl(firestore) }

        single<RestaurantsRepository> { RestaurantsRepositoryImpl(firestore) }

        single<FavoriteRepository> { FavoriteRepositoryImpl(firestore) }

        single<ReviewRepository> { ReviewRepositoryImpl(firestore) }

        single<UserRepository> { UserRepositoryImpl(firebaseAuth, firestore) }

        single<StorageRepository<*>> { StorageRepositoryImpl(firebaseStorage) }

        single<ConfigLoaderRepository>(named(CONFIG_REMOTE)) {
            FirebaseConfigRepository(
                firebaseConfig
            )
        }

        single<ConfigLoaderRepository>(named(CONFIG_LOCAL)) {
            LocalConfigRepository(appPreference)
        }

        single<ConfigWriterRepository> {
            LocalConfigRepository(appPreference)
        }

    }

    private val viewModelModule = module {

        viewModel { MainViewModel(get(), get(), get(), get()) }

        viewModel { DetailViewModel(get(), get(), get(), get(), get()) }

        viewModel { SearchViewModel(get(), get(), get()) }

        viewModel { UserViewModel(get()) }

        viewModel { ProfileViewModel(get(), get(), get()) }

        viewModel { FavoriteViewModel(get()) }

        viewModel { ReviewViewModel(get(), get(), get(), get(named(CONFIG_REMOTE))) }

        viewModel { IntroViewModel(get(), get()) }

    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BaseApplication)
            modules(listOf(repositoryModule, daoModule, viewModelModule))
        }

        if (BuildConfig.DEBUG) {
            Logger.addLogAdapter(AndroidLogAdapter())
        }
    }
}