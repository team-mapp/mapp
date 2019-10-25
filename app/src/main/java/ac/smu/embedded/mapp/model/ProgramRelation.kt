package ac.smu.embedded.mapp.model

data class ProgramRelation(val relatedDocIds: List<String>) {
    companion object {
        fun fromMap(map: Map<String, Any>): ProgramRelation {
            return ProgramRelation(map.keys.toList())
        }
    }
}