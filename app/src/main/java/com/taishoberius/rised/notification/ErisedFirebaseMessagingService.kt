package com.taishoberius.rised.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ErisedFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        if (p0?.data?.get("body") == "toothbrush") {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    RxBus.publish(RxEvent.ToothBrushNotificationEvent(true))
                }
            }
            return
        }

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
            RxBus.publish(RxEvent.PreferenceNotificationEvent(p0?.data?.get("title") ?: ""))
            }
        }
    }
}