package ac.smu.embedded.mapp.service

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Notification
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.repository.local.NotificationDao
import ac.smu.embedded.mapp.restaurantDetail.RestaurantDetailActivity
import ac.smu.embedded.mapp.util.EXTRA_DOCUMENT_ID
import ac.smu.embedded.mapp.util.NotificationConstants.TYPE_REVIEW_CREATED
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FirebaseNotificationService : FirebaseMessagingService() {

    private val userRepository: UserRepository by inject()
    private val restaurantsRepository: RestaurantsRepository by inject()
    private val notificationDao: NotificationDao by inject()

    override fun onNewToken(token: String) {
        Logger.d("Refreshed token: $token")
        userRepository.addNotificationToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Logger.d("Message received (data: ${message.data})")
        val type = message.data[KEY_TYPE]
        when (type) {
            TYPE_REVIEW_CREATED -> {
                val restaurantId = message.data[KEY_RESTAURANT_ID]
                CoroutineScope(Dispatchers.Main).launch {
                    val restaurant =
                        restaurantsRepository.loadRestaurant(restaurantId!!)
                    if (restaurant != null) {
                        createNotificationChannel(
                            CHANNEL_ID_REVIEW_CREATED,
                            getString(R.string.notification_channel_review_created),
                            getString(R.string.notification_channel_review_created_desc),
                            NotificationManagerCompat.IMPORTANCE_DEFAULT
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            val largeIcon = getLargeIcon(restaurant.image)
                            notifyReviewNotification(restaurant, largeIcon)
                            notificationDao.insert(
                                Notification(
                                    type = type,
                                    content = restaurantId,
                                    notifyAt = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createNotificationChannel(
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
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }

    private fun notifyReviewNotification(restaurant: Restaurant, largeIcon: Bitmap) {
        val restaurantIntent = Intent(this, RestaurantDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_DOCUMENT_ID, restaurant.documentId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, restaurantIntent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_REVIEW_CREATED)
            .setSmallIcon(R.drawable.ic_review)
            .setLargeIcon(largeIcon)
            .setContentTitle(getString(R.string.notification_review_created_title))
            .setContentText(
                String.format(
                    getString(R.string.notification_review_created_content),
                    restaurant.name
                )
            )
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID_REVIEW_CREATED, builder.build())
        }
    }

    private fun getLargeIcon(imageUrl: String): Bitmap {
        val future = Glide.with(this)
            .asBitmap()
            .load(Firebase.storage.getReference(imageUrl))
            .apply(RequestOptions.circleCropTransform())
            .submit()
        return future.get()
    }

    companion object {
        private const val KEY_TYPE = "type"
        private const val KEY_RESTAURANT_ID = "restaurantId"

        private const val NOTIFICATION_ID_REVIEW_CREATED = 101

        private const val CHANNEL_ID_REVIEW_CREATED = "review_created"
    }
}