package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Program
import ac.smu.embedded.mapp.model.Program.Companion.fromMap
import ac.smu.embedded.mapp.util.asFlow
import ac.smu.embedded.mapp.util.toObject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface ProgramsRepository {

    suspend fun loadPrograms(): List<Program>?

    fun loadProgramsSync(): Flow<List<Program>?>

    suspend fun loadProgramsByQuery(query: String): List<Program>?

    suspend fun loadProgram(documentId: String): Program?

    suspend fun loadProgramByName(name: String): Program?

}

class ProgramsRepositoryImpl(private val db: FirebaseFirestore) : ProgramsRepository {

    companion object {
        private const val COLLECTION_PATH = "programs"
    }

    @ExperimentalCoroutinesApi
    override fun loadProgramsSync(): Flow<List<Program>?> =
        db.collection(COLLECTION_PATH)
            .asFlow()
            .map { it?.toObject(::fromMap) }

    override suspend fun loadPrograms(): List<Program>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadProgramsByQuery(query: String): List<Program>? =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadProgram(documentId: String): Program? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadProgramByName(name: String): Program? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)
}