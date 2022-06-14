package com.example.segundoevalucacion.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "animal_data_table")
data class Animal(
    @PrimaryKey( autoGenerate = true)
    @ColumnInfo(name = "animal_id")
    var id: Int,
    @ColumnInfo(name = "animal_titulo")
    var titulo: String,
    @ColumnInfo(name = "animal_descripcion")
    var descripcion: String,
    @ColumnInfo(name = "animal_autor")
    var autor: String,
    @ColumnInfo(name = "animal_imagen")
    var imagen: String
)