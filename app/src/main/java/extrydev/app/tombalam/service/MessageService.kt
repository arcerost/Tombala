package extrydev.app.tombalam.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import extrydev.app.tombalam.MainActivity
import extrydev.app.tombalam.R
import extrydev.app.tombalam.repository.TombalaRepository
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@AndroidEntryPoint
class MessageService : FirebaseMessagingService() {

    @Inject
    lateinit var tombalaRepository: TombalaRepository

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        tombalaRepository.saveToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["title"]
        val body = message.data["message"]

        showNotification(title, body)
    }


    private fun showNotification(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.coin)  // Bu, uygulamanızın bildirim ikonudur.
            .setContentTitle(title ?: "Tombala")
            .setContentText(message ?: "Bildirim İçeriği")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID.incrementAndGet(), builder.build())
    }
    companion object {
        const val CHANNEL_ID = "my_notification_channel_id"
        val NOTIFICATION_ID = AtomicInteger(100)
    }
}