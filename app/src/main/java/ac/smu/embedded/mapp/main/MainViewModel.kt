package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.*
import ac.smu.embedded.mapp.repository.*
import ac.smu.embedded.mapp.util.*
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

    fun loadCelebsOnce(): LiveData<Resource<List<Celeb>?>> = celebsRepository.loadCelebsOnce()

    fun loadCeleb(name: String): LiveData<Resource<Celeb?>> = celebsRepository.loadCelebByName(name)

    fun loadPrograms(): LiveData<Resource<List<Program>?>> = programsRepository.loadPrograms()

    fun loadProgramsOnce(): LiveData<Resource<List<Program>?>> =
        programsRepository.loadProgramsOnce()

    fun loadProgram(name: String): LiveData<Resource<Program?>> =
        programsRepository.loadProgramByName(name)

    fun loadRestaurants(): LiveData<Resource<List<Restaurant>?>> =
        restaurantsRepository.loadRestaurants()

    fun loadRestaurantsOnce(): LiveData<Resource<List<Restaurant>?>> =
        restaurantsRepository.loadRestaurantsOnce()

    fun loadRestaurant(name: String): LiveData<Resource<Restaurant?>> =
        restaurantsRepository.loadRestaurantByName(name)

    fun loadCelebRelations(celebDocumentId: String): LiveData<Resource<CelebRelation?>> =
        celebRelationsRepository.loadCelebRelation(celebDocumentId)

    fun loadProgramRelations(programDocumentId: String): LiveData<Resource<ProgramRelation?>> =
        programRelationsRepository.loadProgramRelation(programDocumentId)

    fun loadCelebRelationsByName(name: String): LiveData<Resource<CelebRelation?>>? {
        return celebsRepository.loadCelebByName(name).filter {
            it.status == Status.SUCCESS
        }.switchMap {
            celebRelationsRepository.loadCelebRelation(it.data?.documentId!!)
        }
    }

    fun loadCelebWithRelations(name: String): LiveData<Pair<Resource<Celeb?>, Resource<CelebRelation?>>> {
        return celebsRepository.loadCelebByName(name).filter {
            it.status == Status.SUCCESS
        }.combineLatest {
            celebRelationsRepository.loadCelebRelation(it!!.data?.documentId!!)
        }!!.filter {
            it.first.status == Status.SUCCESS && it.second.status == Status.SUCCESS
        }
    }

    fun loadRestaurantsFromCelebName(name: String): LiveData<Resource<List<Restaurant?>>> {
        return celebsRepository.loadCelebByName(name).filter {
            it.status == Status.SUCCESS
        }.switchMap {
            celebRelationsRepository.loadCelebRelation(it.data?.documentId!!)
        }?.filter {
            it.status == Status.SUCCESS
        }?.switchMap { resource ->
            LiveDataUtil.zip(resource.data?.relatedDocIds!!.map { documentId ->
                restaurantsRepository.loadRestaurant(documentId)
                    .filter { it.status == Status.SUCCESS }
            })
        }!!.map { resources ->
            Resource.success(resources.map { it.data })
        }
    }
}
