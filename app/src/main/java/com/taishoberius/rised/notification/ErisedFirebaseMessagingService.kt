package com.taishoberius.rised.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ErisedFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        Log.d("COUCOU", p0?.data?.get("title"))
        Log.d("COUCOU", p0?.data?.get("body"))

    }
}