package ac.smu.embedded.mapp.model

import ac.smu.embedded.mapp.util.emptyFieldError
import android.graphics.Bitmap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Review(
    @get:Exclude val documentId: String,
    val userId: String,
    val restaurantId: String,
    val createdAt: Timestamp,
    var updatedAt: Timestamp,
    var content: ReviewContent
) {
    companion object {
        const val FIELD_USER_ID = "userId"
        const val FIELD_RESTAURANT_ID = "restaurantId"
        const val FIELD_CREATED_AT = "createdAt"
        const val FIELD_UPDATED_AT = "updatedAt"
        const val FIELD_CONTENT = "content"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): Review {
            return Review(
                documentId,
                userId = map[FIELD_USER_ID] as String?
                    ?: emptyFieldError("Review", documentId, FIELD_USER_ID),
                restaurantId = map[FIELD_RESTAURANT_ID] as String?
                    ?: emptyFieldError("Review", documentId, FIELD_RESTAURANT_ID),
                createdAt = map[FIELD_CREATED_AT] as Timestamp?
                    ?: emptyFieldError("Review", documentId, FIELD_CREATED_AT),
                updatedAt = map[FIELD_UPDATED_AT] as Timestamp?
                    ?: emptyFieldError("Review", documentId, FIELD_UPDATED_AT),
                content = if (isExistContent(map[FIELD_CONTENT]))
                    ReviewContent.fromMap(map[FIELD_CONTENT] as Map<String, Any>)
                else
                    emptyFieldError("Review", documentId, FIELD_CONTENT)
            )
        }

        private fun isExistContent(content: Any?): Boolean {
            val map = content as Map<*, *>?
            return map != null
        }
    }
}