package com.samsantech.souschef.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.samsantech.souschef.R
import com.samsantech.souschef.data.CookingAssistantViewModelProvider
import com.samsantech.souschef.data.Recipe
import com.samsantech.souschef.viewmodel.CookingAssistantViewModel
import androidx.media.app.NotificationCompat.MediaStyle

class CookingAssistantService: Service() {
    private val channelId = "cooking_assistant_channel"

    private val cookingAssistantViewModel: CookingAssistantViewModel
        get() = CookingAssistantViewModelProvider.cookingAssistantViewModel

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

//    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val recipe: Recipe? =
            if (intent?.hasExtra("recipe") == true) {
                intent.getParcelableExtra("recipe")
            } else {
                null
            }

        when(intent?.action) {
            Actions.START.toString() -> start(recipe)
            Actions.STOP.toString() -> stop()
        }

        return START_NOT_STICKY
    }

//    @RequiresApi(Build.VERSION_CODES.Q)
    private fun start(recipe: Recipe?) {
        if (recipe != null) {
            cookingAssistantViewModel.startCookingAssistance(recipe)

            val notification = createNotification(recipe)
            startForeground(1, notification)
        }
    }

    private fun stop() {
        cookingAssistantViewModel.stopCookingAssistance()
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Cooking Assistant Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(recipe: Recipe): Notification {
//        val recipeImageBitmap = BitmapFactory.decodeStream(recipe.recipe.imagePath?.let {
//            this.assets.open(it) })

        val notification =
            NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.cooking_icon)
//            .setLargeIcon()
            .setContentTitle(recipe.title)
            .setContentText("Cooking with SousChef")
            .addAction(createStopNotificationAction())
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(0)
            )

        return notification.build()
    }
    private fun createStopNotificationAction(): NotificationCompat.Action {
        val stopIntent = Intent(this, CookingAssistantNotificationActionReceiver::class.java).apply {
            action = Actions.STOP.toString()
        }

        val stopPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            //necessary to be immutable for Build Version T
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopAction = NotificationCompat.Action.Builder(R.drawable.stop_icon, "Stop", stopPendingIntent).build()

        return stopAction
    }

    enum class Actions {
        START, STOP
    }

}