package ac.smu.embedded.mapp.util

fun String.toNullIfEmpty(): String? = if (this.isEmpty()) null else this