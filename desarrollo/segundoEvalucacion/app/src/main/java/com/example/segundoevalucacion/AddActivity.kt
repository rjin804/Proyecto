package com.example.segundoevalucacion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.segundoevalucacion.api.ApiService
import com.example.segundoevalucacion.api.PixaGson
import com.example.segundoevalucacion.databinding.ActivityAddBinding
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.viewModel.MainViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.String
import kotlin.toString

/**
 * Add activity
 *
 * @constructor Create empty Add activity
 */
class AddActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBinding
    var email=""
    var imagen =""
    var nom =""
    var descripcion =""
    var imgPerfil =""
    var titulo=""
    var id=""
    private val database = Firebase.database
    private lateinit var db: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    lateinit var storageReference: StorageReference

    val URL_BASE = "https://pixabay.com/api/"
    val KEY ="24239096-415e4587ba4fd9f9918dee9d0"

    private val responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                if (it.data!=null){
                    var uri: Uri? = it.data!!.data
                    var imagenref=storageReference.child("/publicacion/imagen/"+id+".png")
                    subirImagen(imagenref, uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title=" "

        //viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        storageReference= Firebase.storage.reference
        initDB()
        cogerDatos()
        setUp()
    }

    private fun cogerDatos() {
        val dato = intent.extras
        email= dato?.getString("EMAIL").toString()
        var user = email.replace(".","")
        val time = System.currentTimeMillis()
        id = user+time
        db.getReference("usuarioPerfil").child(FirebaseAuth.getInstance().uid!!).get().addOnCompleteListener(object: OnCompleteListener<DataSnapshot>{
            override fun onComplete(p0: Task<DataSnapshot>) {
                if (p0.isSuccessful){
                    if(p0.getResult().exists()){
                        val dataSnapshot = p0.getResult()
                        nom = String.valueOf(dataSnapshot.child("nombre").value)
                        imgPerfil = String.valueOf(dataSnapshot.child("img").value)

                        Log.d("NOMBRE>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", nom)
                    }
                }
            }

        })

    }

    private fun setUp() {
        binding.btnGuardar.setOnClickListener {
            guardar()
        }
        binding.btnVolver.setOnClickListener {
            onBackPressed()
        }
        binding.btnLimpiar.setOnClickListener {
            binding.edTitulo.setText("")
            binding.edDescripcion.setText("")
            binding.edTitulo.requestFocus()
        }
        binding.imgAdd1.setOnClickListener {
            imagen()
        }
        binding.imgBuscar.setOnClickListener {
            buscar()
        }

    }

    private fun buscar() {
        binding.searchView.visibility = View.VISIBLE
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: kotlin.String?): Boolean {
                if(!p0.isNullOrEmpty()){
                    getTarjetas(p0.lowercase())
                }
                return true
            }

            override fun onQueryTextChange(p0: kotlin.String?): Boolean {
                return true
            }

        })

    }

    private fun getTarjetas(query: kotlin.String) {
        CoroutineScope(Dispatchers.IO).launch {
            val llamada = getRetrofit().create(ApiService::class.java)
                .getDatosPixaBay("?key=$KEY&q=$query")
            val datos = llamada.body()
            runOnUiThread {
                if (llamada.isSuccessful){
                    val i: PixaGson? = datos?.listaPixaGson?.get(2)

                    imagen = i?.imagen.toString()
                    Toast.makeText(this@AddActivity,"Guardado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@AddActivity,"Error",Toast.LENGTH_LONG).show()
                }
            }

        }
    }
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun imagen() {
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
                        Toast.makeText(this, "Imagen subida con exito.", Toast.LENGTH_LONG).show()
                        cargarFoto()
                    }else{
                        Toast.makeText(this, "Error al guardar la Imagen", Toast.LENGTH_LONG).show()

                    }
                }

        }
    }

    private fun cargarFoto() {
        var imagenref=storageReference.child("/publicacion/imagen/"+id+".png")
        if (imagenref!=null){
            imagenref.downloadUrl
                .addOnSuccessListener {
                    imagen="$it"
                    Picasso.get().load(it).resize(150,150).centerCrop().into(binding.imgView2)
                    binding.imgView2.visibility = View.VISIBLE

                }
                .addOnFailureListener {
                }
        }
    }

    private fun guardar() {

        if(!dato()) return

        var pub = Publicacion(titulo, descripcion,imgPerfil,nom,imagen,email,id,FirebaseAuth.getInstance().uid!!)
        db.getReference("publicacion").child(id).setValue(pub)
            .addOnSuccessListener {
                Toast.makeText(this, "AÑADIDO", Toast.LENGTH_LONG).show()
                onBackPressed()
            }
            .addOnFailureListener {
                Log.d("ERROR DE AÑADIR:>>>>>>", it.message.toString())
                Toast.makeText(this, "ERROR DE AÑADIR", Toast.LENGTH_LONG).show()
            }


    }

    private fun dato(): Boolean {
        descripcion = binding.edDescripcion.text.toString()
        titulo = binding.edTitulo.text.toString()

        if (titulo.length==0){
            binding.edTitulo.setError("Rellena este campo!!!")
            return false
        }

        if(descripcion.length==0){
            binding.edDescripcion.setError("Rellena este campo!!!")
            return false
        }

        return true
    }


    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
    }


}
