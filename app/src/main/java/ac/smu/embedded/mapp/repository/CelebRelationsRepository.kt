package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.CelebRelation
import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore

@Deprecated("셀럽 데이터에 식당의 id를 포함하도록 바꿨음 삭제될 예정")
interface CelebRelationsRepository {

    fun loadCelebRelation(celebDocumentId: String): LiveData<Resource<CelebRelation?>>

}

class CelebRelationsRepositoryImpl(private val db: FirebaseFirestore) : CelebRelationsRepository {

    companion object {
        private const val COLLECTION_PATH = "celebs_relations"
    }

    override fun loadCelebRelation(celebDocumentId: String): LiveData<Resource<CelebRelation?>> {
        return db.collection(COLLECTION_PATH).document(celebDocumentId).get().asLiveData()
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