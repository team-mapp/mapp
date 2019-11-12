package ac.smu.embedded.mapp.model

import ac.smu.embedded.mapp.util.emptyFieldError
import com.google.firebase.firestore.GeoPoint

data class Restaurant(
    val documentId: String,
    val name: String,
    val image: String,
    val additionalImage: List<String>?,
    val address: String,
    val phone: String,
    val location: GeoPoint,
    var isFavorite: Boolean = false
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IMAGE = "image"
        const val FIELD_INDICES = "indices"
        const val FIELD_ADDITIONAL_IMAGE = "additionalImage"
        const val FIELD_ADDRESS = "address"
        const val FIELD_PHONE = "phone"
        const val FIELD_LOCATION = "location"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): Restaurant {
            return Restaurant(
                documentId,
                name = map[FIELD_NAME] as String?
                    ?: emptyFieldError("Restaurant", documentId, FIELD_NAME),
                image = map[FIELD_IMAGE] as String?
                    ?: emptyFieldError("Restaurant", documentId, FIELD_IMAGE),
                additionalImage = map[FIELD_ADDITIONAL_IMAGE] as List<String>?,
                address = map[FIELD_ADDRESS] as String?
                    ?: emptyFieldError("Restaurant", documentId, FIELD_ADDRESS),
                phone = map[FIELD_PHONE] as String?
                    ?: emptyFieldError("Restaurant", documentId, FIELD_PHONE),
                location = map[FIELD_LOCATION] as GeoPoint?
                    ?: emptyFieldError("Restaurant", documentId, FIELD_LOCATION)
            )
        }
    }
}