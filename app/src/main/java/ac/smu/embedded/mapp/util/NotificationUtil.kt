package ac.smu.embedded.mapp.util

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

object NotificationUtil {
    fun createNotificationChannel(
        context: Context,
        id: String,
        name: String,
        desc: String? = null,
        importance: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance).apply {
                if (desc != null) {
                    description = desc
                }
            }
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }
}
