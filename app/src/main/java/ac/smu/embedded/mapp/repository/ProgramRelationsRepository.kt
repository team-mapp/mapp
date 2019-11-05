package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.ProgramRelation
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

interface ProgramRelationsRepository {

    fun loadProgramRelation(celebDocumentId: String): LiveData<Resource<ProgramRelation?>>

}

class ProgramRelationsRepositoryImpl(private val db: FirebaseFirestore) :
    ProgramRelationsRepository {

    companion object {
        private const val COLLECTION_PATH = "programs_relations"
    }

    override fun loadProgramRelation(celebDocumentId: String): LiveData<Resource<ProgramRelation?>> {
        return db.collection(COLLECTION_PATH).document(celebDocumentId).get().asLiveData()
            .map { resource ->
                resource.transform {
                    if (it != null) {
                        ProgramRelation.fromMap(it.data!!)
                    } else {
                        null
                    }
                }
            }
    }
}