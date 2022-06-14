package com.example.segundoevalucacion.notificacion

interface AppConstants {

    companion object{
        const val BASE_URL = "https://fcm.googleapis.com/fcm/send"
        const val SERVER_KEY="AAAASRPbfDo:APA91bFe88nGkHjlmTAz9AKDNu9XJYPGwttLOKMdC76unzOHK9Mom7PBjoZWIYWZrC92okkzmWEj3IImxSoOv7G49BXs82sPzBw1ZkoYBCvFxxWq0aAgf167JCkBj2sMOpy4KOtDfY7l"
        const val CONTENT_TYPE = "application/json"
        const val CHANNEL_ID = "Message Channel"
    }
}