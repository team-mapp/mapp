package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Resource
import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.model.Review.Companion.fromMap
import ac.smu.embedded.mapp.model.ReviewContent
import ac.smu.embedded.mapp.util.asLiveData
import ac.smu.embedded.mapp.util.map
import ac.smu.embedded.mapp.util.observe
import ac.smu.embedded.mapp.util.toObject
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

interface ReviewRepository {

    fun loadReviews(restaurantId: String): LiveData<Resource<List<Review>?>>

    fun loadReviewsSync(scope: CoroutineScope, restaurantId: String): LiveData<List<Review>?>

    suspend fun loadReviewsAwait(restaurantId: String): List<Review>?

    fun loadReviews(restaurantId: String, userId: String): LiveData<Resource<List<Review>?>>

    suspend fun loadReviewsAwait(restaurantId: String, userId: String): List<Review>?

    fun loadUserReviews(userId: String): LiveData<Resource<List<Review>?>>

    suspend fun loadUserReviewsAwait(userId: String): List<Review>?

    fun loadUserReviewsSync(scope: CoroutineScope, userId: String): LiveData<List<Review>?>

    fun loadReview(documentId: String): LiveData<Resource<Review?>>

    suspend fun loadReviewAwait(documentId: String): Review?

    fun addReview(restaurantId: String, userId: String, content: ReviewContent)

    fun updateReview(documentId: String, content: ReviewContent)

    fun removeReview(documentId: String)

}

class ReviewRepositoryImpl(private val db: FirebaseFirestore) : ReviewRepository {

    companion object {
        private const val COLLECTION_PATH = "reviews"
    }

    override fun loadReviews(restaurantId: String): LiveData<Resource<List<Review>?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override fun loadReviewsSync(
        scope: CoroutineScope,
        restaurantId: String
    ): LiveData<List<Review>?> =
        liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH)
                .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
                .observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }

    override suspend fun loadReviewsAwait(restaurantId: String): List<Review>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .get().await()
            .toObject(::fromMap)

    override fun loadReviews(
        restaurantId: String,
        userId: String
    ): LiveData<Resource<List<Review>?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadReviewsAwait(restaurantId: String, userId: String): List<Review>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .get().await()
            .toObject(::fromMap)

    override fun loadUserReviews(userId: String): LiveData<Resource<List<Review>?>> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadUserReviewsAwait(userId: String): List<Review>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .get().await()
            .toObject(::fromMap)

    override fun loadUserReviewsSync(
        scope: CoroutineScope,
        userId: String
    ): LiveData<List<Review>?> =
        liveData(scope.coroutineContext) {
            val channel = db.collection(COLLECTION_PATH)
                .whereEqualTo(Review.FIELD_USER_ID, userId)
                .observe()
            for (snapshot in channel) {
                emit(snapshot.toObject(::fromMap))
            }
        }

    override fun loadReview(documentId: String): LiveData<Resource<Review?>> =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().asLiveData()
            .map { resource ->
                resource.transform {
                    it?.toObject(::fromMap)
                }
            }

    override suspend fun loadReviewAwait(documentId: String): Review? =
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .get().await()
            .toObject(::fromMap)

    override fun addReview(restaurantId: String, userId: String, content: ReviewContent) {
        db.collection(COLLECTION_PATH)
            .add(
                Review(
                    "",
                    restaurantId = restaurantId,
                    userId = userId,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now(),
                    content = content
                )
            )
    }

    override fun updateReview(documentId: String, content: ReviewContent) {
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .update(
                hashMapOf<String, Any>(
                    Review.FIELD_UPDATED_AT to Timestamp.now(),
                    Review.FIELD_CONTENT to content
                )
            )
    }

    override fun removeReview(documentId: String) {
        db.collection(COLLECTION_PATH)
            .document(documentId)
            .delete()
    }
}