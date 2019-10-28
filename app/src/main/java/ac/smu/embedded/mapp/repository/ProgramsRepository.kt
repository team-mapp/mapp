package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

class ProgramsRepository(private val db: FirebaseFirestore) {

    companion object {
        private const val COLLECTION_PATH = "programs"
    }

    fun loadPrograms(): LiveData<Resource<List<Program>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Program.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    fun loadProgramsOnce(): LiveData<Resource<List<Program>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Program.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    fun loadProgram(documentId: String): LiveData<Resource<Program?>> {
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

    fun loadProgramByName(name: String): LiveData<Resource<Program?>> {
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