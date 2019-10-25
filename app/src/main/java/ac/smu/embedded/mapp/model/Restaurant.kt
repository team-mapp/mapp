package ac.smu.embedded.mapp.model

data class Restaurant(val name: String, val image: String) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IMAGE = "image"

        fun fromMap(map: Map<String, Any>): Restaurant {
            return Restaurant(
                name = map[FIELD_NAME] as String? ?: error("Empty $FIELD_NAME"),
                image = map[FIELD_IMAGE] as String? ?: error("Empty $FIELD_IMAGE")
            )
        }
    }
}