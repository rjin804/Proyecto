package com.example.segundoevalucacion.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DAOAnimal {

    @Query("SELECT * FROM animal_data_table")
    fun getAnimal(): LiveData<List<Animal>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addAnimal(animal: Animal)

    @Delete
    fun deleteAnimal(animal: Animal)

    @Update
    fun updateAnimal(animal: Animal)

    @Query("DELETE FROM animal_data_table")
    fun deleteAll()
}