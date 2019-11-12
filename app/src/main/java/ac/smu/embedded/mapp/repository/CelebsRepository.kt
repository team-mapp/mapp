package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Celeb.Companion.fromMap
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

interface CelebsRepository {

    fun loadCelebs(): LiveData<Resource<List<Celeb>?>>

    fun loadCelebsSync(scope: CoroutineScope): LiveData<List<Celeb>?>

    fun loadCelebsOnce(): LiveData<Resource<List<Celeb>?>>

    suspend fun loadCelebsAwait(): List<Celeb>?

    fun loadCelebsByQuery(query: String): LiveData<Resource<List<Celeb>?>>

    suspend fun loadCelebsByQueryAwait(query: String): List<Celeb>?

    fun loadCeleb(documentId: String): LiveData<Resource<Celeb?>>

    suspend fun loadCelebAwait(documentId: String): Celeb?

    fun loadCelebByName(name: String): LiveData<Resource<Celeb?>>

    suspend fun loadCelebByNameAwait(name: String): Celeb?

}

class CelebsRepositoryImpl(private val db: FirebaseFirestore) : CelebsRepository {

    companion object {
        private const val COLLECTION_PATH = "celebs"
    }

    override fun loadCelebs(): LiveData<Resource<List<Celeb>?>> =
        db.collection(COLLECTION_PATH)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }


    override fun loadCelebsSync(scope: CoroutineScope): LiveData<List<Celeb>?> =
        liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH).observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }


    override fun loadCelebsOnce(): LiveData<Resource<List<Celeb>?>> =
        db.collection(COLLECTION_PATH)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }


    override suspend fun loadCelebsAwait(): List<Celeb>? =
        db.collection(COLLECTION_PATH)
            .get().await()
            .toObject(::fromMap)

    override fun loadCelebsByQuery(query: String): LiveData<Resource<List<Celeb>?>> =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadCelebsByQueryAwait(query: String): List<Celeb>? =
        db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await().toObject(::fromMap)

    override fun loadCeleb(documentId: String): LiveData<Resource<Celeb?>> =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadCelebAwait(documentId: String): Celeb? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await().toObject(::fromMap)

    override fun loadCelebByName(name: String): LiveData<Resource<Celeb?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.firstOrNull()?.toObject(::fromMap)
                }
            }

    override suspend fun loadCelebByNameAwait(name: String): Celeb? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Celeb.FIELD_NAME, name)
            .get().await()
            .firstOrNull()
            ?.toObject(::fromMap)
}