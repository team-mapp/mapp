package ac.smu.embedded.mapp.model

data class Program(val documentId: String, val name: String, val image: String) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IMAGE = "image"

        fun fromMap(documentId: String, map: Map<String, Any>): Program {
            return Program(
                documentId,
                name = map[FIELD_NAME] as String? ?: error("Empty $FIELD_NAME"),
                image = map[FIELD_IMAGE] as String? ?: error("Empty $FIELD_IMAGE")
            )
        }
    }
}