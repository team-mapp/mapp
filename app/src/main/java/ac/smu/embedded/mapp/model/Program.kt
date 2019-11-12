package ac.smu.embedded.mapp.model

import ac.smu.embedded.mapp.util.emptyFieldError

data class Program(
    val documentId: String,
    val name: String,
    val image: String,
    val restaurants: List<String>
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IMAGE = "image"
        const val FIELD_INDICES = "indices"
        const val FIELD_RESTAURANTS = "restaurants"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): Program {
            return Program(
                documentId,
                name = map[FIELD_NAME] as String?
                    ?: emptyFieldError("Program", documentId, FIELD_NAME),
                image = map[FIELD_IMAGE] as String?
                    ?: emptyFieldError("Program", documentId, FIELD_IMAGE),
                restaurants = map[FIELD_RESTAURANTS] as List<String>?
                    ?: emptyFieldError("Program", documentId, FIELD_RESTAURANTS)
            )
        }
    }
}