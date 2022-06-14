package com.example.segundoevalucacion.model

import androidx.lifecycle.LiveData

class AnimalRepository (private val animalDao:DAOAnimal)  {

    val animal: LiveData<List<Animal>> = animalDao.getAnimal()


    suspend fun addLibro(ani: Animal) {
        animalDao.addAnimal(ani)
    }

    suspend fun removeLibro(ani: Animal) {
        animalDao.deleteAnimal(ani)
    }

    suspend fun updateLibro(ani: Animal) {
        animalDao.updateAnimal(ani)
    }

    suspend fun borrarTodo() {
        animalDao.deleteAll()
    }
}