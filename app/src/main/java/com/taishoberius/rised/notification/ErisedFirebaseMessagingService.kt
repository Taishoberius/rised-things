package com.taishoberius.rised.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.taishoberius.rised.cross.Rx.RxBus
import com.taishoberius.rised.cross.Rx.RxEvent

class ErisedFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        Log.w("NOTIFICATION", "notification received " + p0?.data?.get("title") ?: "")
        if (p0?.data?.get("body") == "toothbrush") {
            RxBus.publish(RxEvent.ToothBrushNotificationEvent(true))
            return
        }


        RxBus.publish(RxEvent.PreferenceNotificationEvent(p0?.data?.get("title") ?: ""))
    }
}