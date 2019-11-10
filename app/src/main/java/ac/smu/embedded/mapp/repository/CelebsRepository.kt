package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.await
import ac.smu.embedded.mapp.util.map
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

    override fun loadCelebs(): LiveData<Resource<List<Celeb>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Celeb.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    override fun loadCelebsSync(scope: CoroutineScope): LiveData<List<Celeb>?> {
        return liveData(scope.coroutineContext) {
            val snapshot = db.collection(COLLECTION_PATH).await()
            val list =
                snapshot.documents
                    .filter { it.data != null }
                    .map { Celeb.fromMap(it.id, it.data!!) }
            if (list.isNotEmpty()) emit(list.toList())
            else emit(null)
        }
    }

    override fun loadCelebsOnce(): LiveData<Resource<List<Celeb>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Celeb.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    override suspend fun loadCelebsAwait(): List<Celeb>? {
        val snapshot = db.collection(COLLECTION_PATH).get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { Celeb.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
    }

    override fun loadCelebsByQuery(query: String): LiveData<Resource<List<Celeb>?>> {
        return db.collection(COLLECTION_PATH)
            .whereArrayContains(
                Celeb.FIELD_INDICES,
                query
            ).get()
            .asLiveData().map { resource ->
                resource.transform { snapshot ->
                    snapshot?.documents?.map {
                        Celeb.fromMap(it.id, it.data!!)
                    }
                }
            }
    }

    override suspend fun loadCelebsByQueryAwait(query: String): List<Celeb>? {
        val snapshot = db.collection(COLLECTION_PATH)
            .whereArrayContains(Celeb.FIELD_INDICES, query)
            .get().await()
        val list =
            snapshot.documents
                .filter { it.data != null }
                .map { Celeb.fromMap(it.id, it.data!!) }
        return if (list.isNotEmpty()) list else null
    }

    override fun loadCeleb(documentId: String): LiveData<Resource<Celeb?>> {
        return db.collection(COLLECTION_PATH).document(documentId).get().asLiveData()
            .map { resource ->
                resource.transform {
                    if (it != null) {
                        Celeb.fromMap(it.id, it.data!!)
                    } else {
                        null
                    }
                }
            }
    }

    override suspend fun loadCelebAwait(documentId: String): Celeb? {
        val snapshot = db.collection(COLLECTION_PATH).document(documentId).get().await()
        return if (snapshot.data != null) {
            Celeb.fromMap(snapshot.id, snapshot.data!!)
        } else {
            null
        }
    }

    override fun loadCelebByName(name: String): LiveData<Resource<Celeb?>> {
        return db.collection(COLLECTION_PATH).whereEqualTo(
            Celeb.FIELD_NAME,
            name
        ).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                val document = snapshot?.firstOrNull()
                if (document != null) {
                    Celeb.fromMap(document.id, document.data)
                } else {
                    null
                }
            }
        }
    }

    override suspend fun loadCelebByNameAwait(name: String): Celeb? {
        val snapshot = db.collection(COLLECTION_PATH).whereEqualTo(
            Celeb.FIELD_NAME,
            name
        ).get().await()

        val document = snapshot.firstOrNull()
        return if (document != null) {
            Celeb.fromMap(document.id, document.data)
        } else {
            return null
        }
    }
}