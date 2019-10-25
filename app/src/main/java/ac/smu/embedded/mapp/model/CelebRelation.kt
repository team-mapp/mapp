package ac.smu.embedded.mapp.model

data class CelebRelation(val relatedDocIds: List<String>) {
    companion object {
        fun fromMap(map: Map<String, Any>): CelebRelation {
            return CelebRelation(map.keys.toList())
        }
    }
}