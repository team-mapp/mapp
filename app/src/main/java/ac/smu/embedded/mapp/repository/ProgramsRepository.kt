package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.observe
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

interface ProgramsRepository {

    fun loadPrograms(): LiveData<Resource<List<Program>?>>

    fun loadProgramsSync(scope: CoroutineScope): LiveData<List<Program>?>

    fun loadProgramsOnce(): LiveData<Resource<List<Program>?>>

    suspend fun loadProgramsAwait(): List<Program>?

    fun loadProgramsByQuery(query: String): LiveData<Resource<List<Program>?>>

    suspend fun loadProgramsByQueryAwait(query: String): List<Program>?

    fun loadProgram(documentId: String): LiveData<Resource<Program?>>

    suspend fun loadProgramAwait(documentId: String): Program?

    fun loadProgramByName(name: String): LiveData<Resource<Program?>>

    suspend fun loadProgramByNameAwait(name: String): Program?

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

    override fun loadProgramsSync(scope: CoroutineScope): LiveData<List<Program>?> {
        return liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                val list =
                    snapshot.documents
                        .filter { it.data != null }
                        .map { Program.fromMap(it.id, it.data!!) }
                if (list.isNotEmpty()) emit(list.toList())
                else emit(null)
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

    override suspend fun loadProgramsAwait(): List<Program>? {
        val snapshot = db.collection(COLLECTION_PATH).get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { Program.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
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

    override suspend fun loadProgramsByQueryAwait(query: String): List<Program>? {
        val snapshot = db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { Program.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
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

    override suspend fun loadProgramAwait(documentId: String): Program? {
        val snapshot = db.collection(COLLECTION_PATH).document(documentId).get().await()
        return if (snapshot.data != null) {
            Program.fromMap(snapshot.id, snapshot.data!!)
        } else {
            null
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

    override suspend fun loadProgramByNameAwait(name: String): Program? {
        val snapshot = db.collection(COLLECTION_PATH).whereEqualTo(
            Celeb.FIELD_NAME,
            name
        ).get().await()

        val document = snapshot.firstOrNull()
        return if (document != null) {
            Program.fromMap(document.id, document.data)
        } else {
            return null
        }
    }
}