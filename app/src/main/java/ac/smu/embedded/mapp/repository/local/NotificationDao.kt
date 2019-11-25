package ac.smu.embedded.mapp.repository.local

import ac.smu.embedded.mapp.model.Notification
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification ORDER BY notify_at DESC")
    fun loadAll(): Flow<List<Notification>>

    @Update
    suspend fun update(notification: Notification)

    @Insert
    suspend fun insert(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)
}