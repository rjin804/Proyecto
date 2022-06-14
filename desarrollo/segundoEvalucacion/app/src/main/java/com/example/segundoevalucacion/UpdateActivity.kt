package com.example.segundoevalucacion

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.style.UpdateLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.segundoevalucacion.adapter.MensajeAdapter

import com.example.segundoevalucacion.databinding.ActivityUpdateBinding
import com.example.segundoevalucacion.modelo.Mensaje
import com.example.segundoevalucacion.perfil.Perfil
import com.example.segundoevalucacion.preferencia.Prefs
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.viewModel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.net.CookieManager
import java.net.URI
import java.util.*

/**
 * Update activity
 *
 * @constructor Create empty Update activity
 */
class UpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityUpdateBinding
    //private lateinit var viewModel: MainViewModel
    var email=""
    var imagen =""
    var autor =""
    var descripcion =""
    var imgPerfil =""
    var titulo=""
    var id=""
    var img =""
    lateinit var imgview:ImageView
    lateinit var prefs: Prefs
    private val database = Firebase.database
    lateinit var storageReference: StorageReference
    lateinit var messageAdapter: MensajeAdapter
    lateinit var messageList:ArrayList<Mensaje>
    lateinit var reference : DatabaseReference
    private lateinit var db: FirebaseDatabase

    var receiverRoom: String=""
    var senderRoom: String=""

    var uid =""
    var permission = 0
    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            permission= if(it){
                1
            }else{
                0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        storageReference= Firebase.storage.reference
        reference = FirebaseDatabase.getInstance().getReference()
        initDB()
        cogerDato()
        recycler()
        setUp()
    }

    private fun recycler() {

        senderRoom = "ComentarioSender"
        receiverRoom = "ComentarioReceiver"

        messageList = ArrayList()
        messageAdapter = MensajeAdapter(this,messageList,senderRoom,receiverRoom)
        //cogeDato()
        binding.recyclerComentario.layoutManager = LinearLayoutManager(this)

        binding.recyclerComentario.adapter = messageAdapter

        reference.child("publicacion").child(id).child("comentario").child(senderRoom).child("mensaje").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (sna in snapshot.children){

                    val message = sna.getValue<Mensaje>(Mensaje::class.java)
                    message?.mensajeId = sna.key.toString()
                    messageList.add(message!!)
                }
                if(messageList.size>0){
                    binding.recyclerComentario.visibility= View.VISIBLE
                    binding.txtComentario.visibility= View.VISIBLE
                }else{
                    binding.recyclerComentario.visibility= View.GONE
                    binding.txtComentario.visibility= View.GONE
                }
                binding.recyclerComentario.scrollToPosition(messageList.size-1)
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
    }

    private fun setUp() {
        binding.btnModificar.setOnClickListener {
            modificar()
        }
        binding.btn2Volver.setOnClickListener {
            onBackPressed()
        }
        binding.btnBorrar.setOnClickListener {
            borrar()
        }
        binding.imgView3.setOnClickListener {
            responseLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if(permission==1){
                descargar()
            }else{
                Toast.makeText(this, "Activa el permiso",Toast.LENGTH_LONG).show()
            }

        }
        binding.imgComentario2.setOnClickListener {
            val i = Intent(this, ComentarioActivity::class.java).apply {
                putExtra("IMGPERFIL",imgPerfil)
                putExtra("UID",uid)
                putExtra("ID",id)
            }
            startActivity(i)
        }
    }

    private fun descargar()  {
        try {
            val descarga= getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(img))
                .setTitle("IMAGEN "+titulo)
                .setDescription("DESCARGANDO.....")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("image/jpeg")
                .setAllowedOverRoaming(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+titulo+".jpg")


            descarga.enqueue(request)
            Toast.makeText(this,"Descargando..", Toast.LENGTH_SHORT).show()
        }catch (e:Exception){
            Toast.makeText(this,"ERROR DE DESGARCAR", Toast.LENGTH_SHORT).show()
        }

    }

    private fun borrar() {
        /* val animal = Animal(id, titulo,descripcion, autor,imagen)
         viewModel.deleteAnimal(animal)

         Toast.makeText(this, "Borrado", Toast.LENGTH_SHORT).show()
         onBackPressed()*/
        db.getReference("publicacion").child(id).removeValue().addOnSuccessListener {
            Toast.makeText(this,"Publicacion borrado",Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this,"Error de borrar",Toast.LENGTH_SHORT).show()
        }

    }

    private fun modificar() {
        /*descripcion = binding.ed2Descripcion.text.toString()
        titulo = binding.ed2Titulo.text.toString()


        if (!autor.isEmpty() && !titulo.isEmpty() ) {
            imagen="https://via.placeholder.com/150/000000/FFFFFF?text=$titulo"
            val ani = Animal(id, titulo,descripcion, autor, imagen)
            viewModel.updateAnimal(ani)

            Toast.makeText(this, "Modificado", Toast.LENGTH_SHORT).show()
            onBackPressed()
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }*/


        val i = Intent(this, ModificarActivity::class.java).apply {
            putExtra("TITULO", titulo)
            putExtra("DESCRIPCION",descripcion)
            putExtra("AUTOR",autor)
            putExtra("EMAIL",email)
            putExtra("ID",id)
            putExtra("IMG",img)
            putExtra("IMGPERFIL",imgPerfil)
            putExtra("UID",uid)
        }
        startActivity(i)

    }


    private fun cogerDato() {
        val bundle = intent.extras
        autor = bundle?.getString("AUTOR").toString()
        titulo = bundle?.getString("TITULO").toString()
        descripcion= bundle?.getString("DESCRIPCION").toString()
        id = bundle?.getString("ID").toString()
        email = bundle?.getString("EMAIL").toString()
        img = bundle?.getString("IMG").toString()
        imgPerfil = bundle?.getString("IMGPERFIL").toString()
        uid = bundle?.getString("UID").toString()

        binding.ed2Descripcion.setText(descripcion)
        binding.ed2Titulo.setText(titulo)

        if (!img.isEmpty()){
            Picasso.get().load(img).resize(150,150).centerCrop().into(binding.imgView3)
            binding.imgView3.visibility=View.VISIBLE
        }

        if (uid.equals(FirebaseAuth.getInstance().uid)){
            binding.btnBorrar.visibility = View.VISIBLE
            binding.btnModificar.visibility=View.VISIBLE
        }

    }

    /*private fun cargarFoto() {
        var imagenref=storageReference.child("/publicacion/"+email+"/")
        if (imagenref!=null){
            imagenref.downloadUrl
                .addOnSuccessListener {
                    imagen="$it"
                    Picasso.get().load(it).resize(150,150).centerCrop().into(imgview)
                    imgview.visibility = View.VISIBLE

                }
                .addOnFailureListener {

                }
        }
    }*/
}