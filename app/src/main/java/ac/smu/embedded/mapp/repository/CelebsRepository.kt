package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Celeb.Companion.fromMap
import ac.smu.embedded.mapp.util.asFlow
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await

interface CelebsRepository {

    suspend fun loadCelebs(): List<Celeb>?

    fun loadCelebsSync(): LiveData<List<Celeb>?>

    suspend fun loadCelebsByQuery(query: String): List<Celeb>?

    suspend fun loadCeleb(documentId: String): Celeb?

    suspend fun loadCelebByName(name: String): Celeb?

}

class CelebsRepositoryImpl(private val db: FirebaseFirestore) : CelebsRepository {

    companion object {
        private const val COLLECTION_PATH = "celebs"
    }

    override suspend fun loadCelebs(): List<Celeb>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    @ExperimentalCoroutinesApi
    override fun loadCelebsSync(): LiveData<List<Celeb>?> =
        liveData {
            db.collection(COLLECTION_PATH).asFlow().collect {
                emit(it?.toObject(::fromMap))
            }
        }

    override suspend fun loadCelebsByQuery(query: String): List<Celeb>? =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadCeleb(documentId: String): Celeb? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadCelebByName(name: String): Celeb? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)
}