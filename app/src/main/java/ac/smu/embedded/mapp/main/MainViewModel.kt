package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class MainViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository
) : ViewModel() {

    fun loadCelebs() = celebsRepository.loadCelebsSync(viewModelScope)

    fun loadPrograms() = programsRepository.loadProgramsSync(viewModelScope)

}
