package com.example.segundoevalucacion.preferencia

import android.content.Context

class Prefs (c: Context) {
    val FILE_DATA_NAME ="fdn"
    val SHARED_EMAIL ="email"
    val storage = c.getSharedPreferences(FILE_DATA_NAME, 0)
    public fun guardarEmail(email : String){
        storage.edit().putString(SHARED_EMAIL,email).apply()

    }
    public fun leerEmail(): String?{
        return storage.getString(SHARED_EMAIL, null)

    }

    public fun borrarTodo(){
        storage.edit().clear().apply()

    }
}