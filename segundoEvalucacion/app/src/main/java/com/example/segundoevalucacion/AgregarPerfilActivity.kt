package com.example.segundoevalucacion

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.segundoevalucacion.databinding.ActivityAgregarPerfilBinding
import com.example.segundoevalucacion.databinding.DialogoLayoutBinding
import com.example.segundoevalucacion.perfil.Perfil
import com.example.segundoevalucacion.preferencia.Prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.*

class AgregarPerfilActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgregarPerfilBinding
    var email= ""
    var imagen=""
    var nombre=""
    var apellido=""
    var latitud=""
    var longitude=""
    val REQUEST_CODE_TAKE_PHOTO=1
    var id=""

    lateinit var prefs: Prefs
    lateinit var storageReference: StorageReference
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                if (it.data!=null){
                    var uri: Uri? = it.data!!.data
                    var imagenref=storageReference.child(email+"/images/perfil.png")
                    subirImagen(imagenref, uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgregarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="PERFIL"
        storageReference= Firebase.storage.reference


        cogerDatos()
        initDB()
        obtenerDatos()
        setUp()
        cargarFoto()

    }

    private fun cogerDatos() {
        val bundle =intent.extras
        email=bundle?.getString("EMAIL").toString()
        prefs= Prefs(this)
        //email = prefs.leerEmail().toString()
        binding.txt4Email.setText(email)
        Log.d("EMAIL>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><",email)

    }


    private fun cargarFoto() {
        var imagenref=storageReference.child(email+"/images/perfil.png")
        if (imagenref!=null){
            imagenref.downloadUrl
                .addOnSuccessListener {
                    imagen="$it"
                    Picasso.get().load(it).resize(150,150).centerCrop().into(binding.img2Perfil)

                }
                .addOnFailureListener {
                    binding.img2Perfil.setImageDrawable(getDrawable(R.drawable.perfil))
                }
        }
    }

    private fun setUp() {
        binding.img2Perfil.setOnClickListener {
            foto()

        }
        binding.imgChat.setOnClickListener {
            val i = Intent(this, MainChatActivity::class.java).apply {
                putExtra("EMAIL", email)
                putExtra("IMG",imagen)
            }
            startActivity(i)
        }
        binding.imgMapa.setOnClickListener {
            val i = Intent(this, MapsActivity::class.java).apply {
                putExtra("LATITUD", latitud)
                putExtra("LOGITUD",longitude)
            }
            startActivity(i)
        }
        binding.brn4Volver.setOnClickListener {
            onBackPressed()
        }
        binding.btnAddperfil.setOnClickListener {
            dialogo()

        }
        binding.btnAjuste.setOnClickListener {
            startActivity(Intent(this, AjusteActivity::class.java))
        }
        binding.btnPublicacion.setOnClickListener {
            startActivity(Intent(this,MiPublicacionActivity::class.java))
        }
        binding.bntLike.setOnClickListener {
            startActivity(Intent(this,LikeActivity::class.java))
        }
    }

    private fun dialogo() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialog = DialogoLayoutBinding.inflate(inflater)
        builder.setView(dialog.root)
        builder.setPositiveButton("Guardar"){_,_->
            nombre= dialog.nombre.text.toString()
            apellido=dialog.apellido.text.toString()
            longitude=dialog.longitud.text.toString()
            latitud=dialog.latitud.text.toString()


            val perfil = Perfil(email,nombre, apellido, longitude, latitud,  imagen,FirebaseAuth.getInstance().uid)

            db.reference.child("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!).setValue(perfil)
            obtenerDatos()

        }
        builder.setNegativeButton("Cancelar"){_,_->
            Toast.makeText(this,"Has Cancelado",Toast.LENGTH_SHORT).show()
        }
        builder.setCancelable(false)
        builder.create()
        builder.show()


    }

    private fun obtenerDatos() {
        db.getReference("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val nom = snapshot.child("nombre").getValue().toString()
                    val ap = snapshot.child("apellido").getValue().toString()
                    val log = snapshot.child("log").getValue().toString()
                    val lad = snapshot.child("lad").getValue().toString()
                    binding.txt6Nombre.setText(nom)
                    binding.txt6Apellido.setText(ap)
                    binding.txt6Latitud.setText(lad)
                    binding.txt6Longitud.setText(log)
                    if (nom.trim().length!=0){
                        binding.txt6Nombre.visibility=View.VISIBLE
                    }
                    if (ap.trim().length!=0){
                        binding.txt6Apellido.visibility=View.VISIBLE
                    }
                    if (lad.trim().length!=0){
                        binding.txt6Latitud.visibility=View.VISIBLE
                    }
                    if (log.trim().length!=0){
                        binding.txt6Longitud.visibility=View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun foto() {
        val alertDialog= AlertDialog.Builder(this)
            .setTitle("Foto Perfil")
            .setNegativeButton("Galeria"){_,_->
                cambiarFoto()
            }
            .setPositiveButton("Camara") { _,_ ->
                fotoCamara()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun fotoCamara() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicture ->
            takePicture.resolveActivity(this.packageManager)?.also {
                startActivityForResult(takePicture, REQUEST_CODE_TAKE_PHOTO)

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode== RESULT_OK){

            data?.extras?.let { bundle ->

                val imageBitmap = bundle.get("data") as Bitmap
                //binding.img2Perfil.setImageBitmap(imageBitmap)

                val bytes = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val dato = bytes.toByteArray()


                var imagenref=storageReference.child(email+"/images/perfil.png")
                var uploadTask = imagenref.putBytes(dato)
                uploadTask.addOnSuccessListener { taskSnapshotn->
                    val path =MediaStore.Images.Media.insertImage(this.getContentResolver(),imageBitmap,"perfil.png",null)
                    subirImagen(imagenref, Uri.parse(path) )
                }
                .addOnFailureListener{
                    Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    private fun cambiarFoto() {
        var i = Intent(Intent.ACTION_OPEN_DOCUMENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setType("image/*")
        responseLauncher.launch(i)
    }

    private fun subirImagen(imagen: StorageReference, ruta: Uri?) {
        if (ruta!=null){
            imagen.putFile(ruta)
                .addOnCompleteListener(){
                    if (it.isSuccessful){
                        Toast.makeText(this, "Imagen de perfil subida con exito.", Toast.LENGTH_LONG).show()
                        cargarFoto()
                    }else{
                        Toast.makeText(this, "Error al guardar la Imagen", Toast.LENGTH_LONG).show()

                    }
                }

        }
    }
    private fun initDB() {

        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")


    }


}