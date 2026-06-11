package com.example.shoppinglist

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.shoppinglist.domain.ShoppingItem
import com.example.shoppinglist.domain.usecase.GetShoppingListUseCase
import com.example.shoppinglist.domain.usecase.ManageItemUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingForegroundService : Service() {

    @Inject lateinit var getShoppingListUseCase: GetShoppingListUseCase
    @Inject lateinit var manageItemUseCase: ManageItemUseCase

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var currentItem: ShoppingItem? = null
    private val channelId = "shopping_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // В реальном времени следим за базой данных через UseCase
        serviceScope.launch {
            getShoppingListUseCase().collect { list ->
                val nextToBuy = list.find { !it.isBought }
                if (nextToBuy != null) {
                    currentItem = nextToBuy
                    showNotification(nextToBuy)
                } else {
                    stopSelf() // Если некупленных товаров нет, гасим сервис
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_BOUGHT" && currentItem != null) {
            serviceScope.launch {
                manageItemUseCase.toggleBoughtStatus(currentItem!!)
            }
        }
        return START_STICKY
    }

    private fun showNotification(item: ShoppingItem) {
        val boughtIntent = Intent(this, ShoppingForegroundService::class.java).apply {
            action = "ACTION_BOUGHT"
        }
        val boughtPendingIntent = PendingIntent.getService(
            this, 0, boughtIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val activityIntent = Intent(this, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Нужно купить:")
            .setContentText("${item.name} (${item.quantity} шт.)")
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentIntent(activityPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, "Куплено", boughtPendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(notificationId, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(notificationId, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Shopping", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
