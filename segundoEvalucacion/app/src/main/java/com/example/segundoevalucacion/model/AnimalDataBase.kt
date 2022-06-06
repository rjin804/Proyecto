package com.example.segundoevalucacion.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Animal::class], version = 1, exportSchema = false)
abstract class AnimalDataBase : RoomDatabase(){
    abstract val animalDao: DAOAnimal

    companion object{
        @Volatile
        private var INSTANCE: AnimalDataBase?=null
        fun getInstance(c: Context): AnimalDataBase{
            var ins = INSTANCE
            if (ins != null) {
                return ins
            }
            synchronized(this) {
                var instance  = Room.databaseBuilder(
                    c.applicationContext,
                    AnimalDataBase::class.java,
                    "Animal_data_database"
                ).build()
                INSTANCE = instance
                return instance
            }

        }
    }

}