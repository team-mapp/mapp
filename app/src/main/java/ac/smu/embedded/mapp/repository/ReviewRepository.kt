package ac.smu.embedded.mapp.repository

import ac.smu.embedded.mapp.model.Review
import ac.smu.embedded.mapp.model.Review.Companion.fromMap
import ac.smu.embedded.mapp.model.ReviewContent
import ac.smu.embedded.mapp.util.asFlow
import ac.smu.embedded.mapp.util.toObject
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface ReviewRepository {

    fun loadReviewsSync(restaurantId: String): Flow<List<Review>?>

    suspend fun loadReviews(restaurantId: String): List<Review>?

    suspend fun loadReviews(restaurantId: String, userId: String): List<Review>?

    suspend fun loadUserReviews(userId: String): List<Review>?

    fun loadUserReviewsSync(userId: String): Flow<List<Review>?>

    suspend fun loadReview(documentId: String): Review?

    fun addReview(restaurantId: String, userId: String, content: ReviewContent)

    fun updateReview(documentId: String, content: ReviewContent)

    fun removeReview(documentId: String)

    suspend fun hasReviewAwait(restaurantId: String, userId: String): Boolean

}

class ReviewRepositoryImpl(private val db: FirebaseFirestore) : ReviewRepository {

    companion object {
        private const val COLLECTION_PATH = "reviews"
    }

    @ExperimentalCoroutinesApi
    override fun loadReviewsSync(restaurantId: String): Flow<List<Review>?> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .orderBy(Review.FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .asFlow()
            .map { it?.toObject(::fromMap) }

    override suspend fun loadReviews(restaurantId: String): List<Review>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadReviews(restaurantId: String, userId: String): List<Review>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .get().await()
            .toObject(::fromMap)

    override suspend fun loadUserReviews(userId: String): List<Review>? =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .get().await()
            .toObject(::fromMap)

    @ExperimentalCoroutinesApi
    override fun loadUserReviewsSync(userId: String): Flow<List<Review>?> =
        db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .asFlow()
            .map { it?.toObject(::fromMap) }

    override suspend fun loadReview(documentId: String): Review? =
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

    override suspend fun hasReviewAwait(restaurantId: String, userId: String): Boolean {
        val snapshot = db.collection(COLLECTION_PATH)
            .whereEqualTo(Review.FIELD_RESTAURANT_ID, restaurantId)
            .whereEqualTo(Review.FIELD_USER_ID, userId)
            .get().await()
        return snapshot.size() > 0
    }
}