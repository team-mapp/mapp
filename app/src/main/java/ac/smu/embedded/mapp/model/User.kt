package ac.smu.embedded.mapp.model

import ac.smu.embedded.mapp.util.emptyFieldError
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @get:Exclude val uid: String? = null,
    val displayName: String?,
    val profileImage: String?
) {
    fun isExternalProfile(): Boolean = if (profileImage != null) {
        profileImage.startsWith("http") || profileImage.startsWith("https")
    } else {
        false
    }

    companion object {
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_PROFILE_IMAGE = "profileImage"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): User {
            return User(
                uid = documentId,
                displayName = map[FIELD_DISPLAY_NAME] as String?
                    ?: emptyFieldError("User", "none", FIELD_DISPLAY_NAME),
                profileImage = map[FIELD_PROFILE_IMAGE] as String?
                    ?: emptyFieldError("User", "none", FIELD_PROFILE_IMAGE)
            )
        }
    }
}