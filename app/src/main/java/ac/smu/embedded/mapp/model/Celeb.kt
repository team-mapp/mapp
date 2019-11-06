package ac.smu.embedded.mapp.model

data class Celeb(
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
        fun fromMap(documentId: String, map: Map<String, Any>): Celeb {
            return Celeb(
                documentId,
                name = map[FIELD_NAME] as String? ?: error("Empty $FIELD_NAME"),
                image = map[FIELD_IMAGE] as String? ?: error("Empty $FIELD_IMAGE"),
                restaurants = map[FIELD_RESTAURANTS] as List<String>?
                    ?: error("Empty $FIELD_RESTAURANTS")
            )
        }
    }
}