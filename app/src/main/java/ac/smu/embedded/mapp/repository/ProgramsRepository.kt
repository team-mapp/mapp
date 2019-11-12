package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Program.Companion.fromMap
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.observe
import ac.smu.embedded.mapp.util.toObject
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

    override fun loadPrograms(): LiveData<Resource<List<Program>?>> =
        db.collection(COLLECTION_PATH)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override fun loadProgramsSync(scope: CoroutineScope): LiveData<List<Program>?> =
        liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }

    override fun loadProgramsOnce(): LiveData<Resource<List<Program>?>> =
        db.collection(COLLECTION_PATH)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadProgramsAwait(): List<Program>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    override fun loadProgramsByQuery(query: String): LiveData<Resource<List<Program>?>> =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Program.FIELD_INDICES, query)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadProgramsByQueryAwait(query: String): List<Program>? =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
            .toObject(::fromMap)

    override fun loadProgram(documentId: String): LiveData<Resource<Program?>> =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadProgramAwait(documentId: String): Program? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await()
            .toObject(::fromMap)

    override fun loadProgramByName(name: String): LiveData<Resource<Program?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Program.FIELD_NAME, name)
            .get().asLiveData()
            .map { resource ->
                resource.transform { snapshot ->
                    snapshot?.firstOrNull()?.toObject(::fromMap)
                }
            }

    override suspend fun loadProgramByNameAwait(name: String): Program? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)
}