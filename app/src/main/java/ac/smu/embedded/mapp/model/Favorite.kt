package ac.smu.embedded.mapp.model

import ac.smu.embedded.mapp.util.emptyFieldError
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Favorite(
    @get:Exclude val documentId: String,
    val userId: String,
    val restaurantId: String
) {
    companion object {
        const val FIELD_USER_ID = "userId"
        const val FIELD_RESTAURANT_ID = "restaurantId"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): Favorite {
            return Favorite(
                documentId,
                userId = map[FIELD_USER_ID] as String?
                    ?: emptyFieldError("Favorite", documentId, FIELD_USER_ID),
                restaurantId = map[FIELD_RESTAURANT_ID] as String?
                    ?: emptyFieldError("Favorite", documentId, FIELD_RESTAURANT_ID)
            )
        }
    }
}
