package ac.smu.embedded.mapp.service

import ac.smu.embedded.mapp.R
import ac.smu.embedded.mapp.model.Restaurant
import ac.smu.embedded.mapp.repository.RestaurantsRepository
import ac.smu.embedded.mapp.repository.UserRepository
import ac.smu.embedded.mapp.restaurantDetail.RestaurantDetailActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FirebaseNotificationService : FirebaseMessagingService() {

    private val userRepository: UserRepository by inject()
    private val restaurantsRepository: RestaurantsRepository by inject()

    override fun onNewToken(token: String) {
        Logger.d("Refreshed token: $token")
        userRepository.addNotificationToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Logger.d("Message received (data: ${message.data})")
        when (message.data[KEY_DATA_TYPE]) {
            MESSAGE_TYPE_REVIEW_CREATED -> {
                val restaurantId = message.data[KEY_RESTAURANT_ID]
                CoroutineScope(Dispatchers.Main).launch {
                    val restaurant =
                        restaurantsRepository.loadRestaurantAwait(restaurantId!!)
                    if (restaurant != null) {
                        createReviewChannel()
                        createReviewNotification(restaurant)
                    }
                }
            }
        }
    }

    private fun createReviewNotification(restaurant: Restaurant) {
        val restaurantIntent = Intent(this, RestaurantDetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        restaurantIntent.putExtra("document_id", restaurant.documentId)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, restaurantIntent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_REVIEW_CREATED_ID)
            .setSmallIcon(R.drawable.ic_review)
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
            notify(NOTIFICATION_REVIEW_ID, builder.build())
        }
    }

    private fun createReviewChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_review_created)
            val descriptionText = getString(R.string.notification_channel_review_created_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_REVIEW_CREATED_ID, name, importance).apply {
                description = descriptionText
            }
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }


    companion object {
        private val NOTIFICATION_REVIEW_ID = 100

        private val CHANNEL_REVIEW_CREATED_ID = "review_created"

        private val KEY_DATA_TYPE = "type"
        private val KEY_RESTAURANT_ID = "restaurantId"

        private val MESSAGE_TYPE_REVIEW_CREATED = "review_created"
    }
}