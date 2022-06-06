package com.example.segundoevalucacion.adapter

class Mensaje {
    constructor()
    constructor(msj:String, sendId:String,fecha:Long ){
        this.msj = msj
        this.sendId = sendId
        this.fecha = fecha
    }
    var msj: String=""
    var img :String=""
    var fecha = System.currentTimeMillis()

    var mensajeId:String=""
    var sendId:String=""
}