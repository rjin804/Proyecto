package com.example.segundoevalucacion

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segundoevalucacion.adapter.Mensaje
import com.example.segundoevalucacion.adapter.MensajeAdapter
import com.example.segundoevalucacion.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    var email= ""
    var imagen=""
    var uid =""
    var nommbre =""

    private val database = Firebase.database

    private lateinit var db: FirebaseDatabase

    var senderRoom=""
    var recibeRoom =""
    var mensaje:ArrayList<Mensaje>?=null
    var sendUid=""
    var recibeUid=""
    lateinit var adapter: MensajeAdapter
    var dialog:ProgressDialog?=null

    lateinit var storageReference: StorageReference
    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                if (it.data!=null){
                    var uri: Uri? = it.data!!.data
                    val time = System.currentTimeMillis()
                    var imagenref=storageReference.child("mensajesChat").child(time.toString())
                    subirImagen(imagenref, uri)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        title="Chat"
        storageReference= Firebase.storage.reference
        mensaje = ArrayList()
        dialog= ProgressDialog(this)
        dialog!!.setMessage("Actualizado Imagen")
        dialog!!.setCancelable(false)

        initDB()
        cogerGuardar()
        presencia()
        setRecyle()


        setUp()
        estado()

    }
    //estado de usuario
    private fun estado() {
        val handler = Handler()
        binding.txtMensaje.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                db.reference.child("presence").child(sendUid).setValue("Escribiendo...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }
            var userStoppedTyping = Runnable {
                db.reference.child("presence").child(sendUid).setValue("Online")
            }


        })
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun presencia() {
        db.reference.child("presence").child(recibeUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val estado = snapshot.getValue(String::class.java)
                    if(estado=="Offline"){
                        binding.txtPresencia.visibility=View.GONE
                    }else{
                        binding.txtPresencia.setText(estado)
                        binding.txtPresencia.visibility=View.VISIBLE
                    }
                    Log.d("ESTADO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><", estado!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    /*private fun ponerListenerDB() {
        val postListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista : ArrayList<Mensaje> = ArrayList()
                for (data in snapshot.children){
                    val datosMensaje = data.getValue<Mensaje>(Mensaje::class.java)
                    if (datosMensaje!= null){
                        lista.add(datosMensaje)
                    }else continue
                }
                //Ordeno esta lista por marca de tiempo
                lista.sortBy { mensaje -> mensaje.fecha }
                setRecyle(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error:>>>>>>", error.message.toString())
            }

        }
        reference.addValueEventListener(postListener)
    }*/

    private fun setRecyle() {
        senderRoom = sendUid+recibeUid
        recibeRoom = recibeUid+sendUid
        adapter = MensajeAdapter(this, mensaje!!, senderRoom,recibeRoom)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recycle.layoutManager = linearLayoutManager
        binding.recycle.adapter = adapter
        //binding.recycle.scrollToPosition(mensaje?.size?.minus(1)!!)
        db.reference.child("mensajesChat").child(senderRoom!!).child("mensaje")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    mensaje!!.clear()
                    for (sna in snapshot.children){
                        val msj = snapshot.getValue(Mensaje::class.java)
                        msj!!.mensajeId = sna.key!!
                        mensaje!!.add(msj)

                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun setUp() {
        binding.ivSend.setOnClickListener {
            enviarMensaje()
        }
        binding.imgSubir.setOnClickListener{
            subir()

        }
        binding.btnVolver5.setOnClickListener {
            onBackPressed()
        }

    }

    private fun subir() {
        var i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setType("image/*")
        responseLauncher.launch(i)
    }
    private fun subirImagen(imagen: StorageReference, ruta: Uri?) {
        imagen.putFile(ruta!!).addOnCompleteListener{task ->
            if(task.isSuccessful){
                imagen.downloadUrl.addOnSuccessListener {it->
                    val file = it.toString()
                    val txt = binding.txtMensaje.text.toString()
                    val time = System.currentTimeMillis()
                    val key = db.reference.push().key
                    val msj = Mensaje(txt,sendUid,time)
                    msj.msj = "foto"
                    msj.img= file
                    binding.txtMensaje.setText("")

                    val lastMsj = HashMap<String,Any>()
                    lastMsj["lastMsg"] = msj.msj
                    lastMsj["lastMsgTime"] = msj.fecha
                    db.reference.child("mensajesChat").updateChildren(lastMsj)
                    db.reference.child("mensajesChat").child(recibeRoom).updateChildren(lastMsj)
                    db.reference.child("mensajesChat").child(senderRoom).child("mensaje").child(key!!)
                        .setValue(msj).addOnSuccessListener {
                            db.reference.child("mensajesChat").child(recibeRoom).child("mensaje").child(key)
                                .setValue(msj).addOnSuccessListener {
                                    Log.d("SUBIDO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>,", msj.msj)
                                }
                        }
                }
            }

        }
    }


    private fun enviarMensaje() {
        val texto = binding.txtMensaje.text.toString().trim()
        val time = System.currentTimeMillis()
        val key = db.reference.push().key
        val msj = Mensaje(texto,sendUid,time)
        val lastMsj = HashMap<String,Any>()
        lastMsj["lastMsg"] = msj.msj
        lastMsj["lastMsgTime"] = msj.fecha
        db.reference.child("mensajesChat").child(senderRoom).updateChildren(lastMsj)
        db.reference.child("mensajesChat").child(recibeRoom).updateChildren(lastMsj)
        db.reference.child("mensajesChat").child(senderRoom).child("mensaje").child(key!!)
            .setValue(msj).addOnSuccessListener {
                db.reference.child("mensajesChat").child(recibeRoom).child("mensaje").child(key)
                    .setValue(msj).addOnSuccessListener {
                        Log.d("SUBIDO>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>,", msj.msj)
                    }
            }

        /*if (ac == false) {
            if (texto.length == 0) {
                Toast.makeText(this, "Escribe algo", Toast.LENGTH_LONG).show()
                return
            }






            } else {
                texto = "Has subido un foto"
                val time = System.currentTimeMillis()
                val msj = Mensaje(texto, email, imagen)
                msj.ac=true
                msj.url=web
                val key = db.reference.push().key
                val lastMsj = HashMap<String,Any>()
                lastMsj["lastMsg"] = msj.msj
                lastMsj["lastMsgTime"] = msj.fecha
                db.reference.child("chat").child(senderRoom).updateChildren(lastMsj)
                db.reference.child("chat").child(recibeRoom).updateChildren(lastMsj)
                db.reference.child("chat").child(senderRoom).child("mensaje").child(key!!)
                    .setValue(msj).addOnSuccessListener {
                    db.reference.child("chat").child(recibeRoom).child("mensaje").child(key)
                        .setValue(msj).addOnSuccessListener {

                        }
                }

                /*reference.child(time.toString()).setValue(msj)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Enviado", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Log.d("Error en envio:>>>>>>", it.message.toString())
                    }*/
            }*/


            //ac = false
         binding.txtMensaje.setText("")

    }

    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")

    }

    private fun cogerGuardar() {
        val bundle = intent.extras
        email= bundle?.getString("EMAIL").toString()
        imagen=bundle?.getString("IMG").toString()
        nommbre = bundle?.getString("NOMBRE").toString()
        uid = bundle?.getString("UID").toString()
        Picasso.get().load(imagen).resize(150,150).centerCrop().into(binding.imgPerfil2)
        recibeUid = bundle?.getString("uid").toString()
        sendUid = FirebaseAuth.getInstance().uid.toString()
        binding.txtContactor.setText(nommbre)

    }

    override fun onPause() {
        super.onPause()
        var currentId = FirebaseAuth.getInstance().uid
        db.reference.child("presence").child(currentId!!).setValue("Offline")
    }

    override fun onResume() {
        super.onResume()
        var currentId = FirebaseAuth.getInstance().uid
        db.reference.child("presence").child(currentId!!).setValue("Online")
    }
}