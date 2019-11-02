package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

interface ProgramsRepository {

    fun loadPrograms(): LiveData<Resource<List<Program>?>>

    fun loadProgramsOnce(): LiveData<Resource<List<Program>?>>

    fun loadProgramsByQuery(query: String): LiveData<Resource<List<Program>?>>

    fun loadProgram(documentId: String): LiveData<Resource<Program?>>

    fun loadProgramByName(name: String): LiveData<Resource<Program?>>

}

class ProgramsRepositoryImpl(private val db: FirebaseFirestore) : ProgramsRepository {

    companion object {
        private const val COLLECTION_PATH = "programs"
    }

    override fun loadPrograms(): LiveData<Resource<List<Program>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Program.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    override fun loadProgramsOnce(): LiveData<Resource<List<Program>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Program.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    override fun loadProgramsByQuery(query: String): LiveData<Resource<List<Program>?>> {
        return db.collection(COLLECTION_PATH)
            .whereArrayContains(
                Program.FIELD_INDICES,
                query
            ).get()
            .asLiveData().map { resource ->
                resource.transform { snapshot ->
                    snapshot?.documents?.map {
                        Program.fromMap(it.id, it.data!!)
                    }
                }
            }
    }

    override fun loadProgram(documentId: String): LiveData<Resource<Program?>> {
        return db.collection(COLLECTION_PATH).document(documentId).get().asLiveData()
            .map { resource ->
                resource.transform {
                    if (it != null) {
                        Program.fromMap(it.id, it.data!!)
                    } else {
                        null
                    }
                }
            }
    }

    override fun loadProgramByName(name: String): LiveData<Resource<Program?>> {
        return db.collection(COLLECTION_PATH).whereEqualTo(
            Program.FIELD_NAME,
            name
        ).get().asLiveData()
            .map { resource ->
                resource.transform { snapshot ->
                    val document = snapshot?.firstOrNull()
                    if (document != null) {
                        Program.fromMap(document.id, document.data)
                    } else {
                        null
                    }
                }
            }
    }
}