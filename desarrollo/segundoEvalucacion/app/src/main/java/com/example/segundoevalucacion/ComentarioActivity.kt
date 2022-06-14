package com.example.segundoevalucacion

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segundoevalucacion.adapter.ComentarioAdapter
import com.example.segundoevalucacion.adapter.MensajeAdapter
import com.example.segundoevalucacion.databinding.ActivityComentarioBinding
import com.example.segundoevalucacion.modelo.Mensaje
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

/**
 * Comentario activity
 *
 * @constructor Create empty Comentario activity
 */
class ComentarioActivity : AppCompatActivity() {

    lateinit var binding: ActivityComentarioBinding
    lateinit var messageAdapter: ComentarioAdapter
    lateinit var messageList:ArrayList<Mensaje>
    lateinit var reference : DatabaseReference
    private lateinit var db: FirebaseDatabase

    var receiverRoom: String=""
    var senderRoom: String=""
    var senderUid =""
    var receiverUid =""
    var imagenPerfil =""
    var id =""

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
        binding = ActivityComentarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference= Firebase.storage.reference
        val bundle =intent.extras
        //val nombre = bundle?.getString("NOMBRE").toString()
        receiverUid = bundle?.getString("UID").toString()
        imagenPerfil = bundle?.getString("IMGPERFIL").toString()
        id = bundle?.getString("ID").toString()

        db = FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
        db.getReference("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!).get().addOnCompleteListener(object:
            OnCompleteListener<DataSnapshot> {
            override fun onComplete(p0: Task<DataSnapshot>) {
                if (p0.isSuccessful){
                    if(p0.getResult().exists()){
                        val dataSnapshot = p0.getResult()
                        imagenPerfil = java.lang.String.valueOf(dataSnapshot.child("img").value)

                    }
                }
            }

        })

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference = FirebaseDatabase.getInstance().getReference()
        senderRoom = "ComentarioSender"
        receiverRoom = "ComentarioReceiver"

        messageList = ArrayList()
        messageAdapter = ComentarioAdapter(this,messageList,senderRoom,receiverRoom)
        //cogeDato()
        binding.recycle1.layoutManager = LinearLayoutManager(this)

        binding.recycle1.adapter = messageAdapter

        reference.child("publicacion").child(id).child("comentario").child(senderRoom).child("mensaje").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (sna in snapshot.children){

                    val message = sna.getValue<Mensaje>(Mensaje::class.java)
                    message?.mensajeId = sna.key.toString()
                    messageList.add(message!!)
                }

                binding.recycle1.scrollToPosition(messageList.size-1)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        setUp()
    }
    private fun setUp() {
        binding.ivSend1.setOnClickListener {
            enviarMensaje()

        }
        binding.imgSubir3.setOnClickListener {
            enviarImagen()
        }
        binding.btnVolver4.setOnClickListener {
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



                    reference.child("publicacion").child(id).child("comentario").child(senderRoom).child("mensaje").push()
                        .setValue(messageObject).addOnSuccessListener {
                            reference.child("publicacion").child(id).child("comentario").child(receiverRoom).child("mensaje").push()
                                .setValue(messageObject).addOnSuccessListener {
                                }
                        }
                    binding.edMensaje1.setText("")

                }
            }

        }
    }


    private fun enviarMensaje() {
        val messaje = binding.edMensaje1.text.toString()
        val time = System.currentTimeMillis()
        val messageObject = Mensaje(messaje, senderUid!!, time)
        messageObject.aux= true
        messageObject.imagenPerfil=imagenPerfil


        reference.child("publicacion").child(id).child("comentario").child(senderRoom).child("mensaje").push()
            .setValue(messageObject).addOnSuccessListener {
                reference.child("publicacion").child(id).child("comentario").child(receiverRoom).child("mensaje").push()
                    .setValue(messageObject).addOnSuccessListener {
                    }
            }
        binding.edMensaje1.setText("")
    }
}