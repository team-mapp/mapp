package ac.smu.embedded.mapp.detail

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Status
import ac.smu.embedded.mapp.repository.*
import ac.smu.embedded.mapp.util.LiveDataUtil
import ac.smu.embedded.mapp.util.filter
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.switchMap
import androidx.lifecycle.ViewModel

class DetailViewModel(
    private val celebsRepository: CelebsRepository,
    private val celebRelationsRepository: CelebRelationsRepository,
    private val programsRepository: ProgramsRepository,
    private val programRelationsRepository: ProgramRelationsRepository,
    private val restaurantsRepository: RestaurantsRepository
) : ViewModel() {

    fun loadCeleb(documentId: String) = celebsRepository.loadCeleb(documentId)

    fun loadCelebRestaurants(documentId: String) =
        celebRelationsRepository.loadCelebRelation(documentId)
            .filter { it.status == Status.SUCCESS }
            .switchMap { resource ->
                LiveDataUtil.zip(resource.data?.relatedDocIds!!.map { documentId ->
                    restaurantsRepository.loadRestaurant(documentId).filter {
                        it.status == Status.SUCCESS
                    }
                })
            }!!.map { resources -> Resource.success(resources.map { it.data!! }) }

    fun loadProgram(documentId: String) = programsRepository.loadProgram(documentId)

    fun loadProgramRestaurants(documentId: String) =
        programRelationsRepository.loadProgramRelation(documentId)
            .filter { it.status == Status.SUCCESS }
            .switchMap { resource ->
                LiveDataUtil.zip(resource.data?.relatedDocIds!!.map { documentId ->
                    restaurantsRepository.loadRestaurant(documentId).filter {
                        it.status == Status.SUCCESS
                    }
                })
            }!!.map { resources -> Resource.success(resources.map { it.data!! }) }
}