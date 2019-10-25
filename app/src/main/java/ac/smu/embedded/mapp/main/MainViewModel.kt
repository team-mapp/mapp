package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.*
import ac.smu.embedded.mapp.repository.*
import ac.smu.embedded.mapp.util.combineLatest
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.switchMap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository,
    private val restaurantsRepository: RestaurantsRepository,
    private val celebRelationsRepository: CelebRelationsRepository,
    private val programRelationsRepository: ProgramRelationsRepository
) : ViewModel() {
    private val _printLogData = MutableLiveData<String>()
    val printLogData: LiveData<String> = _printLogData

    fun printLog(tag: String, message: String) {
        Log.d("MainViewModel", "($tag) $message")
        _printLogData.value = "($tag) $message"
    }

    fun loadCelebs(): LiveData<Resource<List<Celeb>?>> = celebsRepository.loadCelebs()

    fun loadCelebsSync(): LiveData<Resource<List<Celeb>?>> = celebsRepository.loadCelebsSync()

    fun loadCeleb(name: String): LiveData<Resource<Celeb?>> = celebsRepository.loadCeleb(name)

    fun loadPrograms(): LiveData<Resource<List<Program>?>> = programsRepository.loadPrograms()

    fun loadProgramsSync(): LiveData<Resource<List<Program>?>> =
        programsRepository.loadProgramsSync()

    fun loadProgram(name: String): LiveData<Resource<Program?>> =
        programsRepository.loadProgram(name)

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> =
        restaurantsRepository.loadRestaurants()

    fun loadRestaurantsSync(): LiveData<Resource<List<Restaurant>?>> =
        restaurantsRepository.loadRestaurantsSync()

    fun loadRestaurant(name: String): LiveData<Resource<Restaurant?>> =
        restaurantsRepository.loadRestaurant(name)

    fun loadCelebRelations(celebDocumentId: String): LiveData<Resource<CelebRelation?>> =
        celebRelationsRepository.loadCelebRelation(celebDocumentId)

    fun loadProgramRelations(programDocumentId: String): LiveData<Resource<ProgramRelation?>> =
        programRelationsRepository.loadProgramRelation(programDocumentId)

    fun loadCelebRelationsByName(name: String): LiveData<Resource<CelebRelation?>>? {
        return celebsRepository.loadCeleb(name).switchMap {
            if (it.status == Status.SUCCESS) {
                celebRelationsRepository.loadCelebRelation(it.data?.documentId!!)
            } else {
                null
            }
        }
    }

    fun loadCelebWithRelations(name: String): LiveData<Pair<Resource<Celeb?>, Resource<CelebRelation?>>?>? {
        return celebsRepository.loadCeleb(name).combineLatest {
            if (it?.status == Status.SUCCESS) {
                celebRelationsRepository.loadCelebRelation(it.data?.documentId!!)
            } else {
                null
            }
        }?.map {
            if (it.first.status == Status.SUCCESS && it.second.status == Status.SUCCESS) {
                it
            } else {
                null
            }
        }
    }
}
