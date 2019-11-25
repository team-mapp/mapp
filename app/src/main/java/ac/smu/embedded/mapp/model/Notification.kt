package ac.smu.embedded.mapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

open class NotificationData {
    var data: Any? = null
}

@Entity(ignoredColumns = ["data"])
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "notify_at") val notifyAt: Long,
    val type: String,
    val content: String,
    val extras: String? = null,
    @ColumnInfo(name = "is_read")
    var isRead: Boolean = false
) : NotificationData()