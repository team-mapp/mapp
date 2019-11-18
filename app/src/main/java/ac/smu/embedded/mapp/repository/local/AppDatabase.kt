package ac.smu.embedded.mapp.repository.local

import ac.smu.embedded.mapp.model.Notification
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Notification::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}