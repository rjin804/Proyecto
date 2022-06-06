package com.example.segundoevalucacion.perfil

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Exclude

class Perfil {
    var img:String ?=null
    var email:String ?=null
    var log:String ?=null
    var lad:String ?=null
    var nombre:String ?=null
    var apellido:String ?=null
    var uid :String ?=null


    constructor(){

    }
    constructor( email:String?,nombre:String?, apellido:String?, log:String?,
    lad:String?, img:String?,uid:String?){
        this.nombre=nombre
        this.apellido = apellido
        this.log = log
        this.lad = lad
        this.email = email
        this.img = img
        this.uid= uid

    }

    @Exclude
    fun toMap(): Map<String, Any? >{
        return mapOf(
            "nombre" to nombre,
            "apellido" to apellido,
            "log" to log,
            "lad" to lad,
            "img" to img
        )

    }
}