package ac.smu.embedded.mapp.model

data class Restaurant(val documentId: String, val name: String, val image: String) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IMAGE = "image"
        const val FIELD_INDICES = "indices"

        fun fromMap(documentId: String, map: Map<String, Any>): Restaurant {
            return Restaurant(
                documentId,
                name = map[FIELD_NAME] as String? ?: error("Empty $FIELD_NAME"),
                image = map[FIELD_IMAGE] as String? ?: error("Empty $FIELD_IMAGE")
            )
        }
    }
}