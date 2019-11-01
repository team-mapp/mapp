package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Celeb
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

class CelebsRepository(private val db: FirebaseFirestore) {

    companion object {
        private const val COLLECTION_PATH = "celebs"
    }

    fun loadCelebs(): LiveData<Resource<List<Celeb>?>> {
        return db.collection(COLLECTION_PATH).asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Celeb.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    fun loadCelebsOnce(): LiveData<Resource<List<Celeb>?>> {
        return db.collection(COLLECTION_PATH).get().asLiveData().map { resource ->
            resource.transform { snapshot ->
                snapshot?.documents?.map {
                    Celeb.fromMap(it.id, it.data!!)
                }
            }
        }
    }

    fun loadCelebsByQuery(query: String): LiveData<Resource<List<Celeb>?>> {
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

    fun loadCeleb(documentId: String): LiveData<Resource<Celeb?>> {
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

    fun loadCelebByName(name: String): LiveData<Resource<Celeb?>> {
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
}