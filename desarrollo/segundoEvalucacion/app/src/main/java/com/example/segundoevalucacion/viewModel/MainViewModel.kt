package com.example.segundoevalucacion.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.segundoevalucacion.model.Animal
import com.example.segundoevalucacion.model.AnimalDataBase
import com.example.segundoevalucacion.model.AnimalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel (application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Animal>>
    val read: LiveData<List<Animal>>
    private val repository: AnimalRepository

    init {
        val userDao = AnimalDataBase.getInstance(
            application
        ).animalDao
        repository = AnimalRepository(userDao)
        readAllData = repository.animal
        read = repository.animal
    }

    fun addAnimal(li: Animal){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLibro(li)
        }
    }

    fun updateAnimal(li: Animal){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateLibro(li)
        }
    }

    fun deleteAnimal(li: Animal){
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeLibro(li)
        }
    }

    fun deleteAllAnimal(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.borrarTodo()
        }
    }

}