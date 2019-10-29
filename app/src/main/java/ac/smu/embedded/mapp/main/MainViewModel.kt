package ac.smu.embedded.mapp.main

import ac.smu.embedded.mapp.repository.CelebsRepository
import ac.smu.embedded.mapp.repository.ProgramsRepository
import androidx.lifecycle.ViewModel

class MainViewModel(
    private val celebsRepository: CelebsRepository,
    private val programsRepository: ProgramsRepository
) : ViewModel() {

}
