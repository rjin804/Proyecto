package com.example.segundoevalucacion

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.segundoevalucacion.api.ApiService
import com.example.segundoevalucacion.api.PixaGson
import com.example.segundoevalucacion.databinding.ActivityAddBinding
import com.example.segundoevalucacion.databinding.ActivityModificarBinding
import com.example.segundoevalucacion.preferencia.Prefs
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.viewModel.MainViewModel
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

class ModificarActivity : AppCompatActivity() {

    lateinit var binding: ActivityModificarBinding
    private lateinit var viewModel: MainViewModel
    var email=""
    var imagen =""
    var autor =""
    var descripcion =""
    var imgPerfil =""
    var titulo=""
    var id=""
    var uid =""
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
        setContentView(R.layout.activity_modificar)
        binding = ActivityModificarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title=" "

        storageReference= Firebase.storage.reference
        initDB()
        cogerDato()
        setUp()
    }

    private fun setUp() {
        binding.btnModifica.setOnClickListener {
            modificar()
        }
        binding.btnVolve.setOnClickListener {
            val i = Intent(this, AnimalActivity::class.java)
            startActivity(i )
        }
        binding.imgAdd.setOnClickListener {
            imagen()
        }
        binding.imgSearch.setOnClickListener {
            buscar()
        }

    }

    private fun buscar() {
        binding.searchView1.visibility = View.VISIBLE
        binding.searchView1.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
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
                    Toast.makeText(this@ModificarActivity,"Guardado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@ModificarActivity,"Error",Toast.LENGTH_LONG).show()
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

    private fun modificar() {
        titulo = binding.edTitulo1.text.toString()
        descripcion = binding.edDescripcion1.text.toString()

        var pub = Publicacion(titulo, descripcion,imgPerfil,autor,imagen,email,id,uid)

        db.getReference("publicacion").child(id).updateChildren(pub.toMap()).addOnSuccessListener {
            Toast.makeText(this, "Modificado", Toast.LENGTH_SHORT ).show()
        }
        val i = Intent(this, AnimalActivity::class.java)
        startActivity(i )
    }

    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
    }

    private fun cogerDato() {
        val bundle = intent.extras
        id = bundle?.getString("ID").toString()
        autor = bundle?.getString("AUTOR").toString()
        titulo = bundle?.getString("TITULO").toString()
        descripcion= bundle?.getString("DESCRIPCION").toString()
        id = bundle?.getString("ID").toString()
        email = bundle?.getString("EMAIL").toString()
        imgPerfil = bundle?.getString("IMGPERFIL").toString()
        imagen = bundle?.getString("IMG").toString()
        uid = bundle?.getString("UID").toString()

        if (!imagen.isEmpty()){
            Picasso.get().load(imagen).resize(150,150).centerCrop().into(binding.imgView1)
            binding.imgView1.visibility= View.VISIBLE
        }
        binding.edTitulo1.setText(titulo)
        binding.edDescripcion1.setText(descripcion)

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
                        Toast.makeText(this, "Imagen  subida con exito.", Toast.LENGTH_LONG).show()
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
                    Picasso.get().load(it).resize(150,150).centerCrop().into(binding.imgView1)
                    binding.imgView1.visibility = View.VISIBLE

                }
                .addOnFailureListener {

                }
        }
    }
}