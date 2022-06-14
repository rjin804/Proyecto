package com.example.segundoevalucacion

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segundoevalucacion.adapter.MensajeAdapter
import com.example.segundoevalucacion.databinding.ActivityGrupoBinding
import com.example.segundoevalucacion.modelo.Mensaje
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class GrupoActivity : AppCompatActivity() {
    lateinit var binding: ActivityGrupoBinding
    lateinit var messageAdapter: MensajeAdapter
    lateinit var messageList:ArrayList<Mensaje>
    lateinit var reference : DatabaseReference

    var receiverRoom: String=""
    var senderRoom: String=""
    var senderUid =""
    var receiverUid =""
    var imagenPerfil =""

    lateinit var storageReference: StorageReference
    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                if (it.data!=null){
                    var uri: Uri? = it.data!!.data
                    val time = System.currentTimeMillis()
                    var imagenref=storageReference.child("Mensaje").child(time.toString())
                    subirImagen(imagenref, uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGrupoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference= Firebase.storage.reference
        val bundle =intent.extras
        //val nombre = bundle?.getString("NOMBRE").toString()
        receiverUid = bundle?.getString("UID").toString()
        imagenPerfil = bundle?.getString("IMG").toString()

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference = FirebaseDatabase.getInstance().getReference()
        senderRoom = "GrupoChatSender"
        receiverRoom = "GrupoChatReceiver"

        messageList = ArrayList()
        messageAdapter = MensajeAdapter(this,messageList,senderRoom,receiverRoom)
        //cogeDato()
        binding.recycleGrupo.layoutManager = LinearLayoutManager(this)

        binding.recycleGrupo.adapter = messageAdapter

        reference.child("chat").child(senderRoom).child("mensaje").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (sna in snapshot.children){

                    val message = sna.getValue<Mensaje>(Mensaje::class.java)
                    message?.mensajeId = sna.key.toString()
                    messageList.add(message!!)
                }

                binding.recycleGrupo.scrollToPosition(messageList.size-1)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        setUp()

    }


    private fun setUp() {
        binding.ivSend2.setOnClickListener {
            enviarMensaje()

        }
        binding.imgSubir2.setOnClickListener {
            enviarImagen()
        }
        binding.btnVolver3.setOnClickListener {
            onBackPressed()
        }


    }

    private fun enviarImagen() {
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
                    val messaje = "Has subido una imagen"
                    val time = System.currentTimeMillis()

                    val messageObject = Mensaje(messaje, senderUid!!, time)
                    messageObject.imagen = file
                    messageObject.aux=true
                    messageObject.imagenPerfil=imagenPerfil



                    reference.child("chat").child(senderRoom).child("mensaje").push()
                        .setValue(messageObject).addOnSuccessListener {
                            reference.child("chat").child(receiverRoom).child("mensaje").push()
                                .setValue(messageObject).addOnSuccessListener {
                                }
                        }
                    binding.edMensaje.setText("")

                }
            }

        }
    }


    private fun enviarMensaje() {
        val messaje = binding.edMensaje.text.toString()
        val time = System.currentTimeMillis()
        val messageObject = Mensaje(messaje, senderUid!!, time)
        messageObject.aux= true
        messageObject.imagenPerfil=imagenPerfil



        reference.child("chat").child(senderRoom).child("mensaje").push()
            .setValue(messageObject).addOnSuccessListener {
                reference.child("chat").child(receiverRoom).child("mensaje").push()
                    .setValue(messageObject).addOnSuccessListener {
                    }
            }
        binding.edMensaje.setText("")
    }


}