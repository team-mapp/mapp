package ac.smu.embedded.mapp.util

data class Event<T>(val data: T, val createdAt: Long = System.currentTimeMillis())