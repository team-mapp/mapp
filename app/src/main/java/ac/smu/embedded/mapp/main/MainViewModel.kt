package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MainViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository
) : ViewModel() {

    fun loadCelebs(): LiveData<Resource<List<Celeb>?>> = celebsRepository.loadCelebs()

    fun loadPrograms(): LiveData<Resource<List<Program>?>> = programsRepository.loadPrograms()

}
