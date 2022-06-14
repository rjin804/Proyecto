package com.example.segundoevalucacion.publicacion

import com.google.firebase.database.Exclude


/**
 * Publicacion
 *
 * @constructor Create empty Publicacion
 */
class Publicacion {
    constructor()
    constructor(titulo: String, descripcion: String,imgPerfil: String,autor :String,img :String,email:String,id:String,uid:String){
        this.titulo= titulo
        this.descripcion=descripcion
        this.imgPerfil=imgPerfil
        this.autor=autor
        this.img=img
        this.email=email
        this.id = id
        this.uid=uid

    }
    @Exclude
    fun toMap(): Map<String, Any? >{
        return mapOf(
            "titulo" to titulo,
            "descripcion" to descripcion,
            "img" to img
        )

    }

    var titulo: String=""
    var descripcion: String=""
    var imgPerfil: String=""
    var autor :String=""
    var img :String=""
    var email:String =""
    //var url:String =""
    var fecha = System.currentTimeMillis()
    var id:String =""
    var uid:String =""


}