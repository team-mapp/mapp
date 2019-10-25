package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.CelebRelation
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.transform
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

class CelebRelationsRepository(private val db: FirebaseFirestore) {

    companion object {
        private const val COLLECTION_PATH = "celebs_relations"
    }

    fun loadCelebRelation(celebDocumentId: String): LiveData<Resource<CelebRelation?>> {
        return db.collection(COLLECTION_PATH).document(celebDocumentId).asLiveData()
            .map { resource ->
                resource.transform {
                    if (it != null) {
                        CelebRelation.fromMap(it.data!!)
                    } else {
                        null
                    }
                }
            }
    }
}