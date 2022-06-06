package com.example.segundoevalucacion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.segundoevalucacion.databinding.ActivityAnimalBinding
import com.example.segundoevalucacion.preferencia.Prefs
import com.example.segundoevalucacion.publicacion.Publicacion
import com.example.segundoevalucacion.publicacion.PublicacionAdapter
import com.example.segundoevalucacion.viewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AnimalActivity : AppCompatActivity() {

    lateinit var binding: ActivityAnimalBinding
    private lateinit var viewModel: MainViewModel
    var email=""
    var img =""
    lateinit var prefs: Prefs
    var imagen=""

    private val database = Firebase.database
    var web=""

    private lateinit var db: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title="Publicaciones"
        cogerDatos()
        initDB()
        listener()
        ponerListenerDB()

    }

    private fun ponerListenerDB() {
        val postListener =  object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista : ArrayList<Publicacion> = ArrayList()
                for (data in snapshot.children){
                    val datosMensaje = data.getValue<Publicacion>(Publicacion::class.java)
                    if (datosMensaje!= null){
                        lista.add(datosMensaje)
                    }else continue
                }
                //Ordeno esta lista por marca de tiempo
                lista.sortBy { mensaje -> mensaje.fecha }
                rellenarLayout(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Error:>>>>>>", error.message.toString())
            }

        }
        reference.addValueEventListener(postListener)
    }

    private fun initDB() {
        db= FirebaseDatabase.getInstance("https://segundoevalucion-default-rtdb.firebaseio.com/")
        reference = db.getReference("publicacion")
    }

    private fun cogerDatos() {
        prefs=Prefs(this)
        val dato = intent.extras
        email=dato?.getString("EMAIL").toString()
        prefs.guardarEmail(email)
    }

    private fun listener() {
        binding.btnFloat.setOnClickListener {

            val i = Intent(this, AddActivity::class.java).apply {
                putExtra("EMAIL",email)
            }
            startActivity(i)


        }
        binding.btnNavigation.setOnNavigationItemSelectedListener { item->
            when (item.itemId) {

                R.id.salir->{
                    finishAffinity()
                    System.exit(0)
                    true
                }
                R.id.perfil->{

                    val i = Intent(this, AgregarPerfilActivity::class.java).apply {
                        putExtra("EMAIL",email)
                        Log.d("EMAIL>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><",email)
                    }
                    startActivity(i)
                    true
                }
                R.id.cerra->{
                    cerrarSesion()
                    true
                }
                else->{
                    false
                }
            }

        }
    }

    private fun rellenarLayout(lista: ArrayList<Publicacion> ) {
       // val linearLayoutManager = LinearLayoutManager(this)
       // binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.adapter = PublicacionAdapter(lista)
        binding.recycler.scrollToPosition(lista.size-1)


    }


    private fun cerrarSesion() {
        prefs.borrarTodo()
        FirebaseAuth.getInstance().signOut()
        var i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun borrar() {
        viewModel.deleteAllAnimal()
    }

}