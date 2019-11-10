package ac.smu.embedded.mapp.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserFavorite(
    @get:Exclude val documentId: String,
    val userId: String,
    val restaurantId: String
) {
    companion object {
        const val FIELD_USER_ID = "userId"
        const val FIELD_RESTAURANT_ID = "restaurantId"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): UserFavorite {
            return UserFavorite(
                documentId,
                userId = map[FIELD_USER_ID] as String? ?: error("Empty $FIELD_USER_ID"),
                restaurantId = map[FIELD_RESTAURANT_ID] as String?
                    ?: error("Empty $FIELD_RESTAURANT_ID")
            )
        }
    }
}
