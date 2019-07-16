package com.taishoberius.rised.trajet.service

object URLManager {

    public val url = "https://maps.googleapis.com/maps/api/\""
    public val apiKey = "AIzaSyCXWLtt5fvE9LalSJg49H0HVLDMMy-KO5c"

    fun getBaseURL(): String {
        return url
    }

}