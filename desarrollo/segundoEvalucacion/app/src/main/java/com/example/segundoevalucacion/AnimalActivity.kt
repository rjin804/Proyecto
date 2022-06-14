package com.example.segundoevalucacion

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.segundoevalucacion.databinding.ActivityAnimalBinding
import com.example.segundoevalucacion.fragment.BuscarFragment
import com.example.segundoevalucacion.fragment.HomeFragment
import com.example.segundoevalucacion.fragment.LikeFragment
import com.example.segundoevalucacion.preferencia.Prefs
import com.example.segundoevalucacion.publicacion.Publicacion
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
        //initDB()
        listener()
        //ponerListenerDB()
        binding.toolbar2.title="Publicaciones"
        binding.toolbar2.setBackgroundColor(Color.parseColor("#2196F3"))
        val home = HomeFragment()
        fragmento(home)


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
                //rellenarLayout(lista)
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

        binding.imgSalir.setOnClickListener {
            finishAffinity()
            System.exit(0)
        }
        binding.btnNavigation.setOnNavigationItemSelectedListener { item->
            when (item.itemId) {

                R.id.add->{
                    val i = Intent(this, AddActivity::class.java).apply {
                        putExtra("EMAIL",email)
                    }
                    startActivity(i)
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
                R.id.home->{
                   // binding.btnFloat.visibility=View.VISIBLE
                    binding.toolbar2.title="Publicaciones"
                    binding.toolbar2.setBackgroundColor(Color.parseColor("#2196F3"))
                    val home = HomeFragment()
                    fragmento(home)
                    true
                }
                R.id.favorita->{
                   // binding.btnFloat.visibility=View.INVISIBLE
                    binding.toolbar2.title="Favoritos"
                    binding.toolbar2.setBackgroundColor(Color.parseColor("#E61B1B"))
                    fragmento(LikeFragment())
                    true
                }
                R.id.buscar->{
                   // binding.btnFloat.visibility=View.INVISIBLE
                    binding.toolbar2.title="Buscar"
                    binding.toolbar2.setBackgroundColor(Color.parseColor("#3ADC40"))
                    fragmento(BuscarFragment())
                    true
                }
                else->{
                    false
                }
            }

        }
    }

    private fun fragmento(fra: androidx.fragment.app.Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragment_view, fra)

        transaction.addToBackStack(null)
        transaction.commit()
    }



    private fun borrar() {
        viewModel.deleteAllAnimal()
    }



}





