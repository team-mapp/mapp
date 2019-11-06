package ac.smu.embedded.mapp.model

data class Restaurant(
    val documentId: String,
    val name: String,
    val image: String,
    val additionalImage: List<String>?,
    val address: String,
    val phone: String,
    val lat: Double,
    val lon: Double
) {
    companion object {
        const val FIELD_NAME = "name"
        const val FIELD_IMAGE = "image"
        const val FIELD_INDICES = "indices"
        const val FIELD_ADDITIONAL_IMAGE = "additionalImage"
        const val FIELD_ADDRESS = "address"
        const val FIELD_PHONE = "phone"
        const val FIELD_LAT = "lat"
        const val FIELD_LON = "lon"

        @Suppress("UNCHECKED_CAST")
        fun fromMap(documentId: String, map: Map<String, Any>): Restaurant {
            return Restaurant(
                documentId,
                name = map[FIELD_NAME] as String? ?: error("Empty $FIELD_NAME"),
                image = map[FIELD_IMAGE] as String? ?: error("Empty $FIELD_IMAGE"),
                additionalImage = map[FIELD_ADDITIONAL_IMAGE] as List<String>?,
                address = map[FIELD_ADDRESS] as String? ?: error("Empty $FIELD_ADDRESS"),
                phone = map[FIELD_PHONE] as String? ?: error("Empty $FIELD_PHONE"),
                lat = map[FIELD_LAT] as Double? ?: error("Empty $FIELD_LAT"),
                lon = map[FIELD_LON] as Double? ?: error("Empty $FIELD_LON")
            )
        }
    }
}