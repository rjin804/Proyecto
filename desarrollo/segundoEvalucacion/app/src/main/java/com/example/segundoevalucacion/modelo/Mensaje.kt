package com.example.segundoevalucacion.modelo

class Mensaje {

    constructor()
    constructor(msj:String, send:String,fecha1:Long){
        mensaje = msj
        sendId = send
        fecha = fecha1


    }

    var mensaje: String=""

    var fecha = System.currentTimeMillis()

    var sendId:String=""
    var mensajeId:String =""
    var imagen:String =""
    var aux: Boolean = false
    var imagenPerfil:String = ""
    var uid:String = ""


}